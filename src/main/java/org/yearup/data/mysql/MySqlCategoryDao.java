package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {

        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories;";

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {

            while(resultSet.next()){

                Category category = mapRow(resultSet);
                categories.add(category);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {

        // get category by id
        String sql = "SELECT * FROM categories WHERE category_id=?;";

        Category category = null;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setLong(1, categoryId);

            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    category = mapRow(resultSet);

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    @Override
    public Category create(Category category) {

        // create a new category
        String sql = "INSERT INTO categories(name, description) VALUES(?,?);";

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            preparedStatement.executeUpdate();

            try (
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys()
            ){
                if(generatedKeys.next()){
                    Long id = generatedKeys.getLong(1);
                    category.setCategoryId(Math.toIntExact(id));
                    return category;

                } else {
                    System.out.println("Creating new category was unsuccessful");
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category) {

        // update category
        String sql = "UPDATE categories SET name=?, description=? WHERE category_id=?";

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setLong(3, categoryId);

            preparedStatement.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int categoryId) {

        // delete category
        String sql = "DELETE FROM categories WHERE category_id=?";

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
        ){
            preparedStatement.setLong(1, categoryId);
            preparedStatement.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private Category mapRow(ResultSet row) throws SQLException {

        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
