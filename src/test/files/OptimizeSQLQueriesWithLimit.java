class OptimizeSQLQueriesWithLimit {

    public void literalSQLrequest() {
        dummyCall("SELECT user FROM myTable"); // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT)}}
        dummyCall("SELECT user FROM myTable LIMIT 50"); // Compliant
    }

    @Query("select t from Todo t where t.status != 'COMPLETED'") // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT)}}
    @Query("select t from Todo t where t.status != 'COMPLETED' LIMIT 25") // Compliant

    private void callQuery() {
        String sql1 = "SELECT user FROM myTable"; // Noncompliant {{Optimize Database SQL Queries (Clause LIMIT)}}
        String sql2 = "SELECT user FROM myTable LIMIT 50"; // Compliant
    }

    private void dummyCall(String request) {
    }
}