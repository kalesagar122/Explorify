package com.explorify.companyname.explorify;


public class JobDItem {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    private String jobTitle;

    public String getCompanyJobId() {
        return companyJobId;
    }

    public void setCompanyJobId(String companyJobId) {
        this.companyJobId = companyJobId;
    }

    private String companyJobId;

    public String getYearExpereince() {
        return yearExpereince;
    }

    public void setYearExpereince(String yearExpereince) {
        this.yearExpereince = yearExpereince;
    }

    private String yearExpereince;

    public String getMonthExperience() {
        return monthExperience;
    }

    public void setMonthExperience(String monthExperience) {
        this.monthExperience = monthExperience;
    }

    private String monthExperience;

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    private String jobDetails;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    private String companyName;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String website;

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    private String companyAddress;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public String getPosteddate() {
        return this.posteddate;
    }

    public void setPosteddate(String posteddate) {
        this.posteddate = posteddate;
    }

    private String posteddate;

    public String getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    private String expireDate;

    public JobDItem() {
    }

    public JobDItem(String id, String jobTitle, String companyJobId,String yearExpereince,String monthExperience,String jobDetails,String companyName,String website,String companyAddress, String image, String posteddate,String expireDate) {
        super();
        this.id = id;
        this.jobTitle = jobTitle;
        this.companyJobId = companyJobId;
        this.yearExpereince = yearExpereince;
        this.monthExperience = monthExperience;
        this.jobDetails = jobDetails;
        this.companyName = companyName;
        this.website = website;
        this.companyAddress = companyAddress;
        this.image = image;
        this.posteddate = posteddate;
        this.expireDate = expireDate;
    }
}
