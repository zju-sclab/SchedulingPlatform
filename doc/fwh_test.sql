use car_platform;

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('则通楼附近', '浙江省杭州市浙大路38号浙江大学玉泉校区', 'doc/Map', 0, 0, 0,	0,
             '/yuquan.xml', '/doc/Map/shape/', '浙江省', '杭州市', '西湖区');
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417002', '云乐小蚂蚁', 1, 1, 0, 1, 0, 0, 0, 1);
