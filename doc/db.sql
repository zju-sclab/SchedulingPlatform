drop database if exists car_platform;
create database car_platform;

use car_platform;
set charset utf8;

create table park (
  id int auto_increment primary key comment 'id',
  name varchar(255) not null comment '园区名称',
  `zh` varchar(255) null comment '园区中文名称',

  img_url varchar(255) not null comment '园区地图图片url',
  minx double(10, 7) not null default 0.0 comment 'min longitude',
  miny double(10, 7) not null default 0.0 comment 'min latitude',
  maxx double(10, 7) not null default 0.0 comment 'max longitude',
  maxy double(10, 7) not null default 0.0 comment 'max latitude',

  map_file_url varchar(255) null comment 'map fastdfs url',
  shape_file_url varchar(255) null comment 'shape fastdfs_url',

  province varchar(255) comment '省',
  city varchar(255) null comment '市',
  area varchar(255) null comment '区/县',
  gmt_modify datetime not null default current_timestamp on update current_timestamp comment '园区信息修改时间',
  gmt_create datetime not null default current_timestamp comment '园区添加时间',
  constraint u_name unique key (name)
);

/*
create table car_info
(
  vin varchar(20) primary key comment '车辆vin码',
  car_plate varchar(30) null comment '车牌号',
  park_id int null comment '园区id',
  drive_mode int not null default 1 comment '可以使用的驾驶方式，默认自动驾驶',
  type varchar(100) null comment '类别',
  simulation int default '0' not null comment '是否仿真，默认否',
  gmt_modify datetime null default current_timestamp on update CURRENT_TIMESTAMP comment '记录修改时间',
  gmt_create datetime null default current_timestamp comment '记录添加时间'
  #   store_period int default '1000' not null comment '车端采样周期',
  #   report_period int default '10' not null comment '正常时信息上报周期',
  #   emergency_report_period int default '100' not null comment '异常时上报周期',
  #   heart_beat_period int default '60' not null comment '心跳周期',
  #   terminal_response_timeout int default '1' not null comment '终端响应超时时间，默认1S',
  #   server_response_timeout int default '1' not null comment '服务端响应超时时间，默认1S',
  #   login_interval int default '30' not null comment '登录间隔，默认30min',
  #   pre_on_report_period int default '10' not null comment 'preON状态信息上报周期，默认10S',
  #   wifi_ap_ssid varchar(255) null comment '车载wifi AP ssid',
  #   wifi_ap_password varchar(255) null comment '车载wifi AP password',
  #   bluetooth_mac varchar(255) null comment '车载蓝牙mac地址',
  #   bluetooth_name varchar(255) null comment '车载蓝牙名称',
  #   bluetooth_pair_key varchar(255) null comment '车载蓝牙配对密码'
);
# alter table car_info add constraint car_fk_park foreign key (park_id) references park(id);
*/

create table car_dynamic
(
  vin varchar(255) primary key comment '车辆VIN编码',
  car_plate varchar(30) null comment '车牌号',
  park_id int null comment '园区id',
  type int not null default 0 comment '类别',
  simulation int default '0' not null comment '是否仿真，默认否',
  drive_mode int not null default '1' null comment '驾驶方式，默认自动驾驶',

  use_status int not null default 0 comment '车辆使用状态, rent or reserve is busy 1, return is free 0, default 0, free',
  body_status int not null default 0 comment 'calcifer, device status, default 0, fine',
  module_status int not null default 0 comment 'autonomus terminal info, default 0, fine',
  connect int not null default 0 comment 'calcifer is connect or not, default 0, not connect',
  endurance double(5,2) null default '0.00' not null comment '续航里程',
  energy double(4,2) null default 0.00 comment '电量状态',

  station varchar(255) null comment '车辆所在站点，state=0时有效',
  is_valid int default 0 null comment '定位是否有效，默认无效',
  longitude double(9, 6) default '0' null comment '经度',
  latitude double(8, 6) default '0' null comment '纬度',
  gmt_modify datetime not null default current_timestamp on update CURRENT_TIMESTAMP comment '记录更新时间',
  gmt_create datetime not null default current_timestamp comment '记录插入时间',
  constraint u_vin unique key(vin)
);
alter table car_dynamic add constraint dynamic_fk_park foreign key (park_id) references park(id) on update cascade;


create table user_roles(
  id int auto_increment primary key,
  role varchar(100) not null comment '角色',
  `desc` varchar(255) default null,
  available boolean default false not null,
  gmt_modify datetime null default current_timestamp on update CURRENT_TIMESTAMP,
  unique key (role)
)auto_increment = 101;

create table user_permissions (
  id int auto_increment primary key,
  permission varchar(100) not null comment '资源',
  `desc` varchar(255) default null,
  available boolean default true not null,
  gmt_modify datetime null default current_timestamp on update current_timestamp,
  unique key (permission)
) auto_increment = 1001;

create table role_permission(
  role_id int not null ,
  permission_id int not null ,
  primary key (role_id, permission_id)
);

alter table role_permission add constraint role_id_fk_roles foreign key (role_id) references user_roles(id) on update cascade on delete cascade;
alter table role_permission add constraint permission_id_fk_permissions foreign key (permission_id) references user_permissions(id) on update cascade;

create table user_info
(
  uid int auto_increment primary key comment 'uid',
  role_id int null comment '用户角色类型',
  username varchar(100) null comment '用户名',
  password varchar(255) null comment '密码',
  pwd_key varchar(255) null comment '盐',
  real_name varchar(100) null comment '真实姓名',
  phone_number varchar(100) not null comment '手机号码',
  email varchar(100) null comment '邮箱',
  gmt_modify datetime not null default current_timestamp on update current_timestamp,
  gmt_create datetime not null default current_timestamp,
  constraint u_username unique key(username),
  constraint u_phone unique key(phone_number)
);

alter table user_info add constraint user_role_id_fk_roles foreign key (role_id) references user_roles(id) on update cascade;

/*
create table phone_auth (
  uid int primary key comment '用户唯一识别码',
  phone varchar(100) not null comment '手机号码',
  random_code varchar(10) null comment '随机验证码',
  expire int not null default 300 comment '验证码有效时间，默认300s',
  gmt_modify datetime default current_timestamp on update current_timestamp,
  gmt_create datetime default current_timestamp,
  constraint u_phone unique key(phone)
);

create table pwd_auth (
  uid int primary key comment '用户唯一识别码',
  username varchar(100) null comment '用户名',
  password varchar(100) null comment '密码',
  pwd_key varchar(100) null comment '密码随机盐',
  gmt_modify datetime default current_timestamp on update current_timestamp,
  gmt_create datetime default current_timestamp,
  constraint u_username unique key(username)
);

alter table phone_auth add constraint phone_fk_user foreign key(uid) references user_info(uid) on update cascade on delete cascade;
alter table pwd_auth add constraint pwd_fk_user foreign key (uid) references user_info(uid) on update cascade on delete cascade;
*/
drop table if exists data_record;

CREATE TABLE IF NOT EXISTS `data_record`(
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `vin` VARCHAR(17) NOT NULL ,
  `category` INT NOT NULL ,
  `run_id` VARCHAR(30) NOT NULL ,
  `park_name` VARCHAR(30) NOT NULL ,
  `simulation` BOOLEAN NOT NULL ,
  `url` VARCHAR(30) NOT NULL ,
  `owner` VARCHAR(20) NOT NULL ,
  `size` LONG NOT NULL ,
  `gmt_created` DATETIME DEFAULT now(),
  `gmt_modified` DATETIME DEFAULT now() on update now()
);
