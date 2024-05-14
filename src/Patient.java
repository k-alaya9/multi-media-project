public class Patient {
    private String name;
    private int age;
    private String medicalRecordNo;
    private String nationality;
    private String diagnosis;
    private String date;

    public Patient(String name, int age, String medicalRecordNo, String nationality, String diagnosis, String date) {
        this.name = name;
        this.age = age;
        this.medicalRecordNo = medicalRecordNo;
        this.nationality = nationality;
        this.diagnosis = diagnosis;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMedicalRecordNo() {
        return medicalRecordNo;
    }

    public void setMedicalRecordNo(String medicalRecordNo) {
        this.medicalRecordNo = medicalRecordNo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
