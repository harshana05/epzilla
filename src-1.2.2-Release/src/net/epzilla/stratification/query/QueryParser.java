package net.epzilla.stratification.query;

/**
 * Created by IntelliJ IDEA.
 * User: Rajeev
 * Date: Feb 24, 2010
 * Time: 1:46:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface QueryParser {

    public Query parseString(String query) throws InvalidSyntaxException;

}
