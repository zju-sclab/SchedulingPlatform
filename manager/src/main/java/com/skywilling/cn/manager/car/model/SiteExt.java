package com.skywilling.cn.manager.car.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

    /**
     * ClassName SiteExt
     * Author  Lin
     * Date 2019/5/30 16:56
     **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "SiteExt")
@CompoundIndexes({
        @CompoundIndex(name = "location_index", def = "{'location': '2dsphere'}"),
})
public class SiteExt {
    @Id
    private String plateNo;

    private GeoJsonPoint location;
}
