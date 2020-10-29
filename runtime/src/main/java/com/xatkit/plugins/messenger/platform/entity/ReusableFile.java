package com.xatkit.plugins.messenger.platform.entity;

public class ReusableFile {
    private String attachmentId;
    private String filePath;
    private File file;

    public ReusableFile(File file) { this.file = file; }
    public String getAttachmentId() { return attachmentId; }
    public void setAttachmentId(String attachmentId) { this.attachmentId = attachmentId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }
}
