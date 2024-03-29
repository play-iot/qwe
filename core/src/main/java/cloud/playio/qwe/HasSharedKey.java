package cloud.playio.qwe;

interface HasSharedKey {

    /**
     * Application key to help accessing an application local data
     *
     * @return an application shared key
     * @see SharedDataLocalProxy
     */
    String sharedKey();

}
