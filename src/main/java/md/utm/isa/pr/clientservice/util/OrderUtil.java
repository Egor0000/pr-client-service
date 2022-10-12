package md.utm.isa.pr.clientservice.util;

import java.util.concurrent.atomic.AtomicLong;

public class OrderUtil {
    private static final AtomicLong id = new AtomicLong(0);

    public static long getNextOrderId() {
        return id.incrementAndGet();
    }
}
