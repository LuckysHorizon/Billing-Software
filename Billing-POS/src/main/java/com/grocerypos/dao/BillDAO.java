package com.grocerypos.dao;

import com.grocerypos.database.DBUtil;
import com.grocerypos.model.Bill;
import com.grocerypos.model.BillItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bill entity
 */
public class BillDAO {
    
    private static final String INSERT_BILL = 
        "INSERT INTO bills (bill_number, customer_name, customer_phone, subtotal, " +
        "discount_amount, gst_amount, total_amount, payment_method, payment_status, cashier_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String INSERT_BILL_ITEM = 
        "INSERT INTO bill_items (bill_id, item_id, quantity, unit_price, discount_percentage, " +
        "discount_amount, gst_percentage, gst_amount, line_total) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_BILL = 
        "UPDATE bills SET customer_name=?, customer_phone=?, subtotal=?, discount_amount=?, " +
        "gst_amount=?, total_amount=?, payment_method=?, payment_status=?, " +
        "updated_at=CURRENT_TIMESTAMP WHERE id=?";
    
    private static final String FIND_BY_ID = "SELECT * FROM bills WHERE id=?";
    
    private static final String FIND_BY_BILL_NUMBER = "SELECT * FROM bills WHERE bill_number=?";
    
    private static final String FIND_ALL = "SELECT * FROM bills ORDER BY created_at DESC";
    
    private static final String FIND_BY_DATE_RANGE = 
        "SELECT * FROM bills WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC";
    
    private static final String FIND_BY_CASHIER = 
        "SELECT * FROM bills WHERE cashier_id=? ORDER BY created_at DESC";
    
    private static final String FIND_BILL_ITEMS = 
        "SELECT bi.*, i.name as item_name, i.barcode as item_barcode " +
        "FROM bill_items bi " +
        "JOIN items i ON bi.item_id = i.id " +
        "WHERE bi.bill_id=?";
    
    private static final String DELETE_BILL_ITEMS = "DELETE FROM bill_items WHERE bill_id=?";
    
    private static final String GET_DAILY_SALES = 
        "SELECT DATE(created_at) as sale_date, COUNT(*) as total_bills, " +
        "SUM(total_amount) as total_sales " +
        "FROM bills WHERE DATE(created_at) = ?";
    
    private static final String GET_SALES_BY_MONTH = 
        "SELECT MONTH(created_at) as month, YEAR(created_at) as year, " +
        "COUNT(*) as total_bills, SUM(total_amount) as total_sales " +
        "FROM bills WHERE MONTH(created_at) = ? AND YEAR(created_at) = ?";

    /**
     * Insert a new bill with items
     */
    public int insertBill(Bill bill) throws SQLException {
        Connection connection = null;
        try {
            connection = DBUtil.getConnection(false); // Disable auto-commit
            connection.setAutoCommit(false);
            
            // Insert bill
            int billId = insertBillOnly(bill, connection);
            bill.setId(billId);
            
            // Insert bill items
            for (BillItem billItem : bill.getBillItems()) {
                billItem.setBillId(billId);
                insertBillItem(billItem, connection);
            }
            
            connection.commit();
            return billId;
            
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Insert bill only (without items)
     */
    private int insertBillOnly(Bill bill, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BILL, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, bill.getBillNumber());
            statement.setString(2, bill.getCustomerName());
            statement.setString(3, bill.getCustomerPhone());
            statement.setBigDecimal(4, bill.getSubtotal());
            statement.setBigDecimal(5, bill.getDiscountAmount());
            statement.setBigDecimal(6, bill.getGstAmount());
            statement.setBigDecimal(7, bill.getTotalAmount());
            statement.setString(8, bill.getPaymentMethod().name());
            statement.setString(9, bill.getPaymentStatus().name());
            statement.setInt(10, bill.getCashierId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating bill failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Insert bill item
     */
    private void insertBillItem(BillItem billItem, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BILL_ITEM)) {
            
            statement.setInt(1, billItem.getBillId());
            statement.setInt(2, billItem.getItemId());
            statement.setInt(3, billItem.getQuantity());
            statement.setBigDecimal(4, billItem.getUnitPrice());
            statement.setBigDecimal(5, billItem.getDiscountPercentage());
            statement.setBigDecimal(6, billItem.getDiscountAmount());
            statement.setBigDecimal(7, billItem.getGstPercentage());
            statement.setBigDecimal(8, billItem.getGstAmount());
            statement.setBigDecimal(9, billItem.getLineTotal());
            
            statement.executeUpdate();
        }
    }

    /**
     * Update an existing bill
     */
    public boolean updateBill(Bill bill) throws SQLException {
        Connection connection = null;
        try {
            connection = DBUtil.getConnection(false);
            connection.setAutoCommit(false);
            
            // Update bill
            try (PreparedStatement statement = connection.prepareStatement(UPDATE_BILL)) {
                statement.setString(1, bill.getCustomerName());
                statement.setString(2, bill.getCustomerPhone());
                statement.setBigDecimal(3, bill.getSubtotal());
                statement.setBigDecimal(4, bill.getDiscountAmount());
                statement.setBigDecimal(5, bill.getGstAmount());
                statement.setBigDecimal(6, bill.getTotalAmount());
                statement.setString(7, bill.getPaymentMethod().name());
                statement.setString(8, bill.getPaymentStatus().name());
                statement.setInt(9, bill.getId());
                
                statement.executeUpdate();
            }
            
            // Delete existing bill items
            try (PreparedStatement statement = connection.prepareStatement(DELETE_BILL_ITEMS)) {
                statement.setInt(1, bill.getId());
                statement.executeUpdate();
            }
            
            // Insert new bill items
            for (BillItem billItem : bill.getBillItems()) {
                billItem.setBillId(bill.getId());
                insertBillItem(billItem, connection);
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Find bill by ID
     */
    public Bill findById(int id) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Bill bill = mapResultSetToBill(resultSet);
                    bill.setBillItems(findBillItems(id));
                    return bill;
                }
                return null;
            }
        }
    }

    /**
     * Find bill by bill number
     */
    public Bill findByBillNumber(String billNumber) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BILL_NUMBER)) {
            
            statement.setString(1, billNumber);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Bill bill = mapResultSetToBill(resultSet);
                    bill.setBillItems(findBillItems(bill.getId()));
                    return bill;
                }
                return null;
            }
        }
    }

    /**
     * Find all bills
     */
    public List<Bill> findAll() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Bill bill = mapResultSetToBill(resultSet);
                bill.setBillItems(findBillItems(bill.getId()));
                bills.add(bill);
            }
        }
        
        return bills;
    }

    /**
     * Find bills by date range
     */
    public List<Bill> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_DATE_RANGE)) {
            
            statement.setDate(1, Date.valueOf(startDate.toLocalDate()));
            statement.setDate(2, Date.valueOf(endDate.toLocalDate()));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bill bill = mapResultSetToBill(resultSet);
                    bill.setBillItems(findBillItems(bill.getId()));
                    bills.add(bill);
                }
            }
        }
        
        return bills;
    }

    /**
     * Find bills by cashier
     */
    public List<Bill> findByCashier(int cashierId) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CASHIER)) {
            
            statement.setInt(1, cashierId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bill bill = mapResultSetToBill(resultSet);
                    bill.setBillItems(findBillItems(bill.getId()));
                    bills.add(bill);
                }
            }
        }
        
        return bills;
    }

    /**
     * Find bill items for a specific bill
     */
    public List<BillItem> findBillItems(int billId) throws SQLException {
        List<BillItem> billItems = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BILL_ITEMS)) {
            
            statement.setInt(1, billId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    billItems.add(mapResultSetToBillItem(resultSet));
                }
            }
        }
        
        return billItems;
    }

    /**
     * Get daily sales summary
     */
    public Object[] getDailySales(java.sql.Date date) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_DAILY_SALES)) {
            
            statement.setDate(1, date);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Object[]{
                        resultSet.getDate("sale_date"),
                        resultSet.getInt("total_bills"),
                        resultSet.getBigDecimal("total_sales")
                    };
                }
                return new Object[]{date, 0, BigDecimal.ZERO};
            }
        }
    }

    /**
     * Map ResultSet to Bill object
     */
    private Bill mapResultSetToBill(ResultSet resultSet) throws SQLException {
        Bill bill = new Bill();
        bill.setId(resultSet.getInt("id"));
        bill.setBillNumber(resultSet.getString("bill_number"));
        bill.setCustomerName(resultSet.getString("customer_name"));
        bill.setCustomerPhone(resultSet.getString("customer_phone"));
        bill.setSubtotal(resultSet.getBigDecimal("subtotal"));
        bill.setDiscountAmount(resultSet.getBigDecimal("discount_amount"));
        bill.setGstAmount(resultSet.getBigDecimal("gst_amount"));
        bill.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        bill.setPaymentMethod(Bill.PaymentMethod.valueOf(resultSet.getString("payment_method")));
        bill.setPaymentStatus(Bill.PaymentStatus.valueOf(resultSet.getString("payment_status")));
        bill.setCashierId(resultSet.getInt("cashier_id"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            bill.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            bill.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return bill;
    }

    /**
     * Map ResultSet to BillItem object
     */
    private BillItem mapResultSetToBillItem(ResultSet resultSet) throws SQLException {
        BillItem billItem = new BillItem();
        billItem.setId(resultSet.getInt("id"));
        billItem.setBillId(resultSet.getInt("bill_id"));
        billItem.setItemId(resultSet.getInt("item_id"));
        billItem.setItemName(resultSet.getString("item_name"));
        billItem.setItemBarcode(resultSet.getString("item_barcode"));
        billItem.setQuantity(resultSet.getInt("quantity"));
        billItem.setUnitPrice(resultSet.getBigDecimal("unit_price"));
        billItem.setDiscountPercentage(resultSet.getBigDecimal("discount_percentage"));
        billItem.setDiscountAmount(resultSet.getBigDecimal("discount_amount"));
        billItem.setGstPercentage(resultSet.getBigDecimal("gst_percentage"));
        billItem.setGstAmount(resultSet.getBigDecimal("gst_amount"));
        billItem.setLineTotal(resultSet.getBigDecimal("line_total"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            billItem.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return billItem;
    }
}
