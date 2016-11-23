package com.explorify.companyname.explorify;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;


/**
 * Created by sagar and sayali on 11-01-2016.
 */
@Table(name = "GeofenceModels")
public class GeofenceModel extends Model {

    @Column(name = "GId", notNull = true)
    public String GId;

    @Column(name = "Lat", notNull = true)
    public Double Lat;

    @Column(name = "Lan", notNull = true)
    public Double Lan;

    @Column(name = "Radius", notNull = true)
    public Float Radius;

    @Column(name = "Title", notNull = true)
    public String Title;

    @Column(name = "Description", notNull = true)
    public String Description;

    @Column(name = "Expiration", notNull = true)
    public Date Expiration;

    public GeofenceModel() {
        super();
    }

    public GeofenceModel(String GId, Double Lat, Double Lan, Float Radius, String Title, String Descriptions, Date Expiration) {
        super();
        this.GId = GId;
        this.Lat = Lat;
        this.Lan = Lan;
        this.Radius = Radius;
        this.Title = Title;
        this.Description = Descriptions;
        this.Expiration = Expiration;
    }

    public static List<GeofenceModel> getExpireGeofences() {
        return new Delete()
                .from(GeofenceModel.class)
                .where("Expiration < ?", new Date().getTime())
                .execute();
    }

    public static GeofenceModel getGeofenceModel(String id) {
        return new Select()
                .from(GeofenceModel.class)
                .where("GId = ?", id)
                .executeSingle();
    }

    public static List<GeofenceModel> getAllGeofences() {
        return new Select()
                .from(GeofenceModel.class)
                .execute();
    }
}
