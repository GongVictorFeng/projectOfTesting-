package com.techyourchance.unittesting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DemoTest {
    private Demo SUT;
//    private AdditionTD mAdd;
    @Mock Addition nAdd;
    @Before
    public void setUp() {
//        mAdd=new AdditionTD();
//        SUT=new Demo(mAdd);
        SUT=new Demo(nAdd);
        success();
    }

    @Test
    public void getNum_success_numberReturned() {
        int result=SUT.getNumber(7,5);
        assertThat(result,is(20));
    }

//    public static class AdditionTD extends Addition{
//
//        @Override
//        public int getNumber(int a, int b) {
//            return a*b;
//        }
//    }

    private void success() {
        when(nAdd.getNumber(anyInt(),anyInt())).thenReturn(new Integer(20));
    }

}