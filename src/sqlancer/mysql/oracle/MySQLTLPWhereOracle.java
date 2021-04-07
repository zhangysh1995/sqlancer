package sqlancer.mysql.oracle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.Randomly;
import sqlancer.mysql.MySQLGlobalState;
import sqlancer.mysql.MySQLVisitor;

public class MySQLTLPWhereOracle extends MySQLQueryPartitioningBase {

    public MySQLTLPWhereOracle(MySQLGlobalState state) {
        super(state);
    }

    @Override
    public void check() throws SQLException {
        super.check();
        select.setWhereClause(null);
        String originalQueryString = MySQLVisitor.asString(select);
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);

        if (Randomly.getBoolean()) {
            select.setOrderByExpressions(gen.generateOrderBys());
        }
        select.setOrderByExpressions(Collections.emptyList());
        select.setWhereClause(predicate);
        String firstQueryString = MySQLVisitor.asString(select);
        select.setWhereClause(negatedPredicate);
        String secondQueryString = MySQLVisitor.asString(select);
        select.setWhereClause(isNullPredicate);
        String thirdQueryString = MySQLVisitor.asString(select);
        List<String> combinedString = new ArrayList<>();

        try(FileWriter fw = new FileWriter("sql5000.sql", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(originalQueryString);
            //more code
            out.println(combinedString);
            //more code
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }

        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,
                thirdQueryString, combinedString, Randomly.getBoolean(), state, errors);
        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
                state);
    }

}
