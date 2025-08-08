package com.sludi.sludi.DTO;

public class DocumentResponse {
    private Long id;
    private String documentType;
    private String fileName;
    private String ipfsHash;
    private Long fileSize;
    private String uploadDate;
    private Boolean verified;

    // Constructors
    public DocumentResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getIpfsHash() { return ipfsHash; }
    public void setIpfsHash(String ipfsHash) { this.ipfsHash = ipfsHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}