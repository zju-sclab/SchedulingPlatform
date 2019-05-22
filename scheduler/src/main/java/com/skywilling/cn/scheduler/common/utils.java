package com.skywilling.cn.scheduler.common;

import com.skywilling.cn.common.model.Orientation;
import com.skywilling.cn.common.model.Position;

import java.io.*;
import java.util.List;


public class utils {
    public static double distance2points(Position p1, Position p2){
        return Math.abs(Math.sqrt(Math.pow(p1.getX() - p2.getX(),2)+Math.pow(p1.getY()-p2.getY(),2)));
    }

    public static EulerAngle Quaternion2Euler(Orientation orientation){
        EulerAngle angle = new EulerAngle();
        double q0 = orientation.getW();
        double q1 = orientation.getX();
        double q2 = orientation.getY();
        double q3 = orientation.getZ();

        double roll = Math.atan2(2.0*(q2*q3+q0*q1), q0*q0-q1*q1-q2*q2+q3*q3);
        double pitch = Math.asin(2.0*(q0*q2-q1*q3));
        double yaw = Math.atan2(2.0*(q1*q2+q0*q3), q0*q0+q1*q1-q2*q2-q3*q3);

        angle.setRoll(roll);
        angle.setPitch(pitch);
        angle.setYaw(yaw);

        return angle;
    }

    public static Orientation Euler2Quaternion(EulerAngle angle){
        Orientation orientation = new Orientation();
        double cosRoll = Math.cos(angle.getRoll() * 0.5);
        double sinRoll = Math.sin(angle.getRoll() * 0.5);

        double cosPitch = Math.cos(angle.getPitch() * 0.5);
        double sinPitch = Math.sin(angle.getPitch() * 0.5);

        double cosYaw = Math.cos(angle.getYaw() * 0.5);
        double sinYaw = Math.sin(angle.getYaw() * 0.5);

        double q0 = cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw;
        double q1 = sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw;
        double q2 = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
        double q3 = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;

        orientation.setX(q1);
        orientation.setY(q2);
        orientation.setZ(q3);
        orientation.setW(q0);
        return orientation;
    }

    public static <T> List<T> depcopy(List<T> srcList){
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try{
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>)inStream.readObject();
            return destList;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

