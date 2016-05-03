package org.testcontainers.images.builder.dockerfile.statement;

import org.junit.Test;

public class RawStatementTest extends AbstractStatementTest {

    @Test
    public void simpleTest() throws Exception {
        assertStatement(new RawStatement("TEST", "value" + System.lineSeparator() + "as\t\\" + System.lineSeparator() + "is"));
    }

}