use car_platform;

# truncate table park;
# truncate table car_dynamic;

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_001', '宣城001', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');
