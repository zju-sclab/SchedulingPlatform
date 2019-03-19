use car_platform;

# truncate table park;
# truncate table car_dynamic;

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_001', '宣城001', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_002', '宣城002', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_003', '宣城003', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_004', '宣城004', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');

insert into park (name, zh, img_url, minx, miny, maxx, maxy, map_file_url, shape_file_url, province, city, area) VALUE
  ('xc_005', '宣城006', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRhXKAP4WaAAbTFPw2bHA025.png', -70, -170, 34,	10,
             'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAUQ_KAAAIbsQfL9E167.xml', 'http://192.168.1.147:80/group1/M00/00/00/wKgBk1tRexqAM-r8AALHWJxoPMg324.xml', '安徽省', '宣城市', '宣州');



insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417009', '皖P 00009', 1, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417008', '皖P 00008', 2, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417007', '皖P 00007', 3, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417006', '皖P 00006', 4, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417005', '皖P 00005', 5, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417004', '皖P 00004', null, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417003', '皖P 00003', null, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417002', '皖P 00002', null, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('00000000112417001', '皖P 00001', null, 1, 0, 1, 0, 0, 0, 1);

insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000008', '皖P S0005', 1, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000007', '皖P S0005', 2, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000006', '皖P S0005', 3, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000005', '皖P S0005', 4, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000004', '皖P S0004', 5, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000003', '皖P S0003', null, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000002', '皖P S0002', null, 1, 0, 1, 0, 0, 0, 1);
insert into car_dynamic (vin, car_plate, park_id, type, simulation, drive_mode, use_status, body_status, module_status, connect) value ('S0000000000000001', '皖P S0001', null, 1, 0, 1, 0, 0, 0, 1);


# truncate table user_info;
# truncate table role_permission;
# truncate table user_roles;
# truncate table user_permissions;


insert into user_permissions (permission, `desc`, available) VALUE ('home.html', '首页', true);


insert into user_roles (role, `desc`, available) value ('admin', '超级管理员', true);
insert into user_roles (role, `desc`, available) value ('parker', '园区管理员', true);
insert into user_roles (role, `desc`, available) value ('guest', '游客', true);

