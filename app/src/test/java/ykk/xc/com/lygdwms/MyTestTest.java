package ykk.xc.com.lygdwms;

import org.junit.Test;

import java.math.BigDecimal;

import ykk.xc.com.lygdwms.comm.Comm;
import ykk.xc.com.lygdwms.util.BigdecimalUtil;

public class MyTestTest {

    @Test
    public void main() {
        double a = 1.195;
        System.out.print(BigdecimalUtil.round(a, 1));
    }
}