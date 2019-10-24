package com.jakdor.labday.UITests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Suite for running Espresso UI tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({LoginFragmentTest.class, MainFragmentTest.class, TimetableFragmentTest.class})
public class UITests {
}
