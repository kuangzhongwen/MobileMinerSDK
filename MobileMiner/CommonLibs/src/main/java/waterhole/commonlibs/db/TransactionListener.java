package waterhole.commonlibs.db;

/**
 * Listener for the transaction's execution process.
 *
 * @author kzw on 2017/03/17
 */
interface TransactionListener {

    /**
     * Called before the transaction begins.
     */
    void onTransactionBegin();

    /**
     * Called after the transaction ends.
     *
     * @param success If the transaction has been set successful.
     */
    void onTransactionEnd(boolean success);
}
