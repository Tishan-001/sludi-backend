package com.sludi.sludi.DTO;

public class DocumentUploadRequest {
    private String nic;
    private String documentType;
    private String fileName;
    private String description;

    // Constructors
    public DocumentUploadRequest() {}

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
