package com.yrgo.dataaccess;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CustomerDaoJdbcTemplateImpl implements  CustomerDao {

   private JdbcTemplate template;

   private static final String CREATE_TABLE_CUSTOMER = "CREATE TABLE CUSTOMER (CUSTOMERID integer generated by default as identity (start with 1), COMPANYNAME VARCHAR(60), EMAIL VARCHAR(40), TELEPHONE VARCHAR(20), NOTES VARCHAR(300))";
    private static final String CREATE_TABLE_CALLS = "CREATE TABLE TBL_CALL (CALLID integer generated by default as identity (start with 1), CUSTOMERID integer, TIMEANDDATE DATE, NOTES VARCHAR(300))";
    private static final String INSERT_CUSTOMER_SQL = "insert into CUSTOMER (COMPANYNAME, EMAIL, TELEPHONE, NOTES) values (?, ?, ?, ?) ";
    private static final String INSERT_CALL_SQL = "insert into TBL_CALL (CUSTOMERID, TIMEANDDATE, NOTES) values (?, ?, ?) ";
    private static final String GET_ALL_CUSTOMERS_SQL = "select * from CUSTOMER";
    private static final String GET_ALL_CALLS_SQL = "select * from TBL_CALL";
    private static final String SELECT_CUSTOMER_WHERE_ID = "SELECT * FROM CUSTOMER WHERE CUSTOMERID=?";


    public CustomerDaoJdbcTemplateImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void create(Customer customer) {
        template.update(INSERT_CUSTOMER_SQL, customer.getCompanyName(), customer.getEmail(), customer.getTelephone(), customer.getNotes());
    }

    private void createTables() {
        try{
            template.update(CREATE_TABLE_CUSTOMER);
            template.update(CREATE_TABLE_CALLS);
        }catch (org.springframework.jdbc.BadSqlGrammarException e){
            System.out.println("Assuming the Customer or the Calls table exists");
        }
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        return template.queryForObject(SELECT_CUSTOMER_WHERE_ID, new CustomerMapper(), customerId);
    }

    @Override
    public List<Customer> getByName(String name) {
        return this.template.query("select * from Customer where CompanyName=?", new CustomerMapper(), name);
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        this.template.update(INSERT_CUSTOMER_SQL, customerToUpdate.getCompanyName(), customerToUpdate.getEmail(), customerToUpdate.getTelephone(), customerToUpdate.getNotes());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        this.template.update("Delete from Customer where CustomerId=?" ,
                oldCustomer.getCustomerId());
    }

    @Override
    public List<Customer> getAllCustomers() {
        return this.template.query(GET_ALL_CUSTOMERS_SQL, new CustomerMapper());
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {
        Customer customer = template.queryForObject(SELECT_CUSTOMER_WHERE_ID, new CustomerMapper(), customerId);
        List<Call> calls = template.query("select * from TBL_CALL where CustomerId=?", new CallMapper(), customerId);
        customer.setCalls(calls);
        return customer;
    }

    @Override
    public void addCall(Call newCall, String customerId) throws RecordNotFoundException {
        template.update(INSERT_CALL_SQL, customerId, newCall.getTimeAndDate(), newCall.getNotes());
        template.update("UPDATE CUSTOMER SET Notes = CONCAT(Notes, ?) WHERE CustomerId = ?", newCall.getNotes(), customerId);
    }
}

class CustomerMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNumber) throws SQLException {
        String customerId = rs.getString("CustomerId");
        String companyName = rs.getString("CompanyName");
        String email = rs.getString("Email");
        String telephone = rs.getString("Telephone");
        String notes = rs.getString("Notes");

        Customer customer = new Customer(customerId, companyName, email, telephone, notes);
        return customer;
    }
}

class CallMapper implements RowMapper<Call> {
    @Override
    public Call mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Date timeAndDate = rs.getDate("TimeAndDate");
        String notes = rs.getString("Notes");

        Call call = new Call(notes, timeAndDate);
        return call;
    }
}

