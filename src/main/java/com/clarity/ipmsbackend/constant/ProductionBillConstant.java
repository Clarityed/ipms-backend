package com.clarity.ipmsbackend.constant;

/**
 * 生产单据常量
 *
 * @author: clarity
 * @date: 2023年03月23日 15:10
 */

public class ProductionBillConstant {

    /**
     * 生产任务单 Production task order PRODUCTION_TASK_ORDER
     */
    public final static String PRODUCTION_TASK_ORDER = "生产任务单";

    /**
     * 生产领料单 Production picking order PRODUCTION_PICKING_ORDER
     */
    public final static String PRODUCTION_PICKING_ORDER = "生产领料单";

    /**
     * 生产退料单 Production return order PRODUCTION_RETURN_ORDER
     */
    public final static String PRODUCTION_RETURN_ORDER = "生产退料单";

    /**
     * 生产入库单 Production receipt order PRODUCTION_RECEIPT_ORDER
     */
    public final static String PRODUCTION_RECEIPT_ORDER = "生产入库单";

    /**
     * 生产退库单 Production stock return order PRODUCTION_STOCK_RETURN_ORDER
     */
    public final static String PRODUCTION_STOCK_RETURN_ORDER = "生产退库单";

    /**
     * 已完工 finished
     */
    public final static int FINISHED = 1;

    /**
     * 未完工 unfinished
     */
    public final static int UNFINISHED = 0;
}
