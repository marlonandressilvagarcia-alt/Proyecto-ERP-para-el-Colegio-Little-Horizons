package com.littlehorizons.erp.model;

/**
 * Guardian / Legal tutor of students.
 */
public class Guardian {
    private int id;
    private String docType;
    private String docNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String relationship;
    private String mainPhone;
    private String secondaryPhone;
    private String email;
    private String address;
    private String occupation;
    private boolean isFinancialResponsible;

    public Guardian() {}

    public Guardian(int id, String docType, String docNumber, String firstName, String middleName,
                    String lastName, String secondLastName, String relationship, String mainPhone,
                    String secondaryPhone, String email, String address, String occupation,
                    boolean isFinancialResponsible) {
        this.id = id;
        this.docType = docType;
        this.docNumber = docNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.relationship = relationship;
        this.mainPhone = mainPhone;
        this.secondaryPhone = secondaryPhone;
        this.email = email;
        this.address = address;
        this.occupation = occupation;
        this.isFinancialResponsible = isFinancialResponsible;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        if (secondLastName != null && !secondLastName.trim().isEmpty()) {
            sb.append(" ").append(secondLastName);
        }
        return sb.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getDocNumber() { return docNumber; }
    public void setDocNumber(String docNumber) { this.docNumber = docNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSecondLastName() { return secondLastName; }
    public void setSecondLastName(String secondLastName) { this.secondLastName = secondLastName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getMainPhone() { return mainPhone; }
    public void setMainPhone(String mainPhone) { this.mainPhone = mainPhone; }

    public String getSecondaryPhone() { return secondaryPhone; }
    public void setSecondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public boolean isFinancialResponsible() { return isFinancialResponsible; }
    public void setFinancialResponsible(boolean financialResponsible) { isFinancialResponsible = financialResponsible; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - Tel: %s - Email: %s", id, getFullName(), relationship, mainPhone, email);
    }
}
