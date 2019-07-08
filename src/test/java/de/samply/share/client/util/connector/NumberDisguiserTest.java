package de.samply.share.client.util.connector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)

public class NumberDisguiserTest {

    private int number;
    private int lower;
    private int upper;

    public NumberDisguiserTest(int number, int lower, int upper) {

        this.number = number;
        this.lower = lower;
        this.upper = upper;

    }

    @Parameterized.Parameters(name = "{index}: Result for {0} should be either {1} or {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 0},
                {1, 10, 10},
                {2, 10, 10},
                {3, 10, 10},
                {7, 10, 10},
                {8, 10, 20},
                {9, 10, 20},
                {17, 20, 20},
                {18, 20, 30},
                {19, 20, 30},

        });
    }

    @Test
    public void testGetDisguisedNumber() {

        List<Integer> possibleValues = new ArrayList<>();
        possibleValues.add(lower);
        possibleValues.add(upper);

        assertThat(possibleValues, hasItems(NumberDisguiser.getDisguisedNumber(number)));

    }
}
