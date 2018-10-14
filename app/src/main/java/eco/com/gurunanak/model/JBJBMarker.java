package eco.com.gurunanak.model;

import java.io.Serializable;
import java.util.List;

public class JBJBMarker implements Serializable {


    private List<MasterBean> master;

    public List<MasterBean> getMaster() {
        return master;
    }

    public void setMaster(List<MasterBean> master) {
        this.master = master;
    }

    public static class MasterBean {
        /**
         * full_name : Satnam
         * email : s02@gmail.com
         * phone_number : 9876543211
         * organization_name : gurukirpa
         * id : 27
         * location : Punjab, India
         * longitude : 75.34121789999999
         * latitude : 31.147130500000006
         * date_planted : 2018-08-27T00:00:00.000Z
         * total_trees_planted : 6
         * plants_types : fg
         * remarks : vhh
         */

        private String full_name;
        private String email;
        private String phone_number;
        private String organization_name;
        private int id;
        private String location;
        private String longitude;
        private String latitude;
        private String date_planted;
        private int total_trees_planted;
        private String plants_types;
        private String remarks;
        private String status;


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getOrganization_name() {
            return organization_name;
        }

        public void setOrganization_name(String organization_name) {
            this.organization_name = organization_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getDate_planted() {
            return date_planted;
        }

        public void setDate_planted(String date_planted) {
            this.date_planted = date_planted;
        }

        public int getTotal_trees_planted() {
            return total_trees_planted;
        }

        public void setTotal_trees_planted(int total_trees_planted) {
            this.total_trees_planted = total_trees_planted;
        }

        public String getPlants_types() {
            return plants_types;
        }

        public void setPlants_types(String plants_types) {
            this.plants_types = plants_types;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
