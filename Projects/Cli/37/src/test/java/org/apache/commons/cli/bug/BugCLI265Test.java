package org.apache.commons.cli.bug;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for CLI-265.
 * <p>
 * The issue is that a short option with an optional value will use whatever comes next as value.
 */
public class BugCLI265Test {

    private DefaultParser parser;
    private Options options;

    @Before
    public void setUp() throws Exception {
        parser = new DefaultParser();

        Option TYPE1 = Option.builder("t1").hasArg().numberOfArgs(1).optionalArg(true).argName("t1_path").build();
        Option LAST = Option.builder("last").hasArg(false).build();

        options = new Options().addOption(TYPE1).addOption(LAST);
    }

    @Test
    public void shouldParseShortOptionWithValue() throws Exception {
        String[] shortOptionWithValue = new String[]{"-t1", "path/to/my/db"};

        final CommandLine commandLine = parser.parse(options, shortOptionWithValue);

        assertEquals("path/to/my/db", commandLine.getOptionValue("t1"));
        assertFalse(commandLine.hasOption("last"));
    }

    @Test
    public void shouldParseShortOptionWithoutValue() throws Exception {
        String[] twoShortOptions = new String[]{"-t1", "-last"};

        final CommandLine commandLine = parser.parse(options, twoShortOptions);

        assertTrue(commandLine.hasOption("t1"));
        assertNotEquals("Second option has been used as value for first option", "-last", commandLine.getOptionValue("t1"));
        assertTrue("Second option has not been detected", commandLine.hasOption("last"));
    }

}
