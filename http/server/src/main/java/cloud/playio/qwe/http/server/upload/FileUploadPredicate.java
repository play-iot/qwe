package cloud.playio.qwe.http.server.upload;

import cloud.playio.qwe.dto.msg.RequestData;

public interface FileUploadPredicate {

    static FileUploadPredicate acceptAll() {
        return (requestData, fileUpload) -> true;
    }

    /**
     * To determine how many upload files
     *
     * @return the number of file, {@code nbOfFiles <= 0} means unlimited
     */
    default int nbOfFiles() {
        return -1;
    }

    /**
     * Test the uploaded file is suitable to proceed
     *
     * @param requestData the request data
     * @param fileUpload  the file upload
     * @return true if satisfied
     */
    boolean test(RequestData requestData, FileUploadWrapper fileUpload);

}
