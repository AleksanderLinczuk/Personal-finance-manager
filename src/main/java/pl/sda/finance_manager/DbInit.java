package pl.sda.finance_manager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbInit {
    private final Connection connection;

    public DbInit(Connection connection) {
        this.connection = connection;
    }

    public void initDb() throws SQLException, IOException {
        try (InputStream categoriesResource = getClass().getResourceAsStream("/sql/categories_ddl.sql");
             InputStream incomesResource = getClass().getResourceAsStream("/sql/incomes_ddl.sql");
             InputStream expensesResource = getClass().getResourceAsStream("/sql/expenses_ddl.sql")) {

            executeSqlFromResource(categoriesResource);
            executeSqlFromResource(incomesResource);
            executeSqlFromResource(expensesResource);
        }
    }

    public void executeSqlFromResource(InputStream inputStream) throws IOException, SQLException {
        if (inputStream == null) {
            System.out.println("Failed to read file!");
            return;
        }
        String sql = new String(inputStream.readAllBytes());

        PreparedStatement ps = connection.prepareStatement(sql);

        System.out.println("Going to execute sql: \n" + sql);
        ps.execute();

        System.out.println("SQL executed successfully!");


    }
}
