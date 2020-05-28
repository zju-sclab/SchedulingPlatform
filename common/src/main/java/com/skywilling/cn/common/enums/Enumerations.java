package com.skywilling.cn.common.enums;

public class Enumerations {

  public enum AckType {
    SUCCESS(0x01, "data is ok"),
    NOT_AUTHORIZED(0x02, " not login"),
    ILLEGAL_TOKEN(0x03, ""),
    ILLEGAL_PROTOCOL(0x04, ""),
    REPEATED(0x05, ""),
    NOT_SUPPORTED_PROTOCOL(0x06, ""),
    SERVER_ERROR(0x07, ""),
    COMMAND(0xFE, "data is command");


    private int ack;
    private String desc;

    AckType(int ack, String desc) {
      this.ack = ack;
      this.desc = desc;
    }

    public static AckType fromValue(int v) {
      for (AckType ack : AckType.values()) {
        if (ack.ack == v) {
          return ack;
        }
      }
      return null;
    }

    public int getAck() {
      return ack;
    }

    public String getDesc() {
      return desc;
    }
  }

  public enum CommandType {

    CAR_REGISTER(0x90, "register"),
    CAR_HEARTBEAT(0x91, "heartbeat"),
    CAR_LOGOUT(0x92, "logout"),
    TASK_START(0xb0, "errand start"),
    TASK_STOP(0xb1, "errand stop"),
    TASK_REPORT(0xb3, "errand report"),
    TASK_QUERY(0xb4, "errand query");


    private final int type;
    private final String desc;

    CommandType(int type, String desc) {
      this.type = type;
      this.desc = desc;
    }

    public static CommandType fromValue(int type) {
      for (CommandType c : CommandType.values()) {
        if (c.type == type) {
          return c;
        }
      }
      return null;
    }

    public int getType() {
      return type;
    }

    public String getDesc() {
      return desc;
    }
  }

  public enum EncryptionMethod {
    DEFAULT(1, "no encryption method"),
    RSA(2, "RSA encryption method"),
    AES128(3, "AES128 encryption method");
    private int type;
    private String desc;

    EncryptionMethod(int method, String desc) {
      this.type = method;
      this.desc = desc;
    }

    public static EncryptionMethod fromType(int type) {
      for (EncryptionMethod method : EncryptionMethod.values()) {
        if (method.type == type) {
          return method;
        }
      }
      return null;
    }

    public int getMethod() {
      return type;
    }

    public String getDesc() {
      return desc;
    }
  }

  public static enum TerminalInstruction {
    UPGRADE(0x01, "update terminal"),
    SHUTDOWN(0x02, "terminal shut down"),
    RESET(0x03, "terminal reset"),
    REBOOT(0x04, "reboot terminal"),
    DISCONNECT(0x05, "disconnect with platform"),
    SAMPLE(0x07, " start monitor system"),;

    private int command;
    private String desc;

    TerminalInstruction(int command, String desc) {
      this.command = command;
      this.desc = desc;
    }

    public static TerminalInstruction fromCommand(int command) {
      for (TerminalInstruction instruction : TerminalInstruction.values()) {
        if (instruction.command == command) {
          return instruction;
        }
      }
      return null;
    }

    public int getCommand() {
      return command;
    }

    public String getDesc() {
      return desc;
    }

    @Override
    public String toString() {
      return "TerminalInstruction{" +
              "command=" + command +
              ", desc='" + desc + '\'' +
              '}';
    }
  }
  /**
   *
   * */
  public enum JobType {
    FILE_SYNC(0x01, "file sync"),
    MANUAL_DRIVE(0x80, "manual drive"),
    AUTO_DRIVE(0x81, "auto drive");

    int type;
    String desc;

    JobType(int type, String desc) {
      this.type = type;
      this.desc = desc;
    }

    public static JobType valueOf(Integer code) {
      for (JobType type : JobType.values()) {
        if (type.type == code) {
          return type;
        }
      }
      return null;
    }

  }
  /**
   *  this class is designed for container-manager.
   *  Todo: move it to container-manager in the near future.
   * */
  public enum CarStatusType {
    REST(0x00,"Rest"),
    LOG_COLLECT(0x01, "COLLECT_LOG"),
    DISCONNECTED(0x02, "disconnected"),
    MANUAL_DRIVING(0x80,"manual driving"),
    AUTONOMOUS_DRIVING(0x81,"task driving"),
    LOG_OUT(0x02, "log out"),
    HEARTBEAT_TIME_OUT(4,"heartBeat Out Of Time"),
    ERROR(5,"error");

    int type;
    String des;

    CarStatusType(int type,String des) {
      this.type = type;
      this.des=des;
    }

    public static CarStatusType valueOf(Integer code) {
      for (CarStatusType type : CarStatusType.values()) {
        if (type.type == code) {
          return type;
        }
      }
      return null;
    }

    public static CarStatusType valueof(String des) {
      for (CarStatusType type : CarStatusType.values()) {
        if (type.des.equals(des)) {
          return type;
        }
      }
      return null;
    }

    public int getType() {
      return type;
    }

    public String getDes() {
      return des;
    }
  }
  /**
   * this class is designed for container-manager.
   * Todo: Move it to container-manager.
   * */
  public enum JobStatusType {
    STARTED(0x01, "job has sent"),
    RUNNING(0x80, "job is running"),
    FINISHED(0x81, "job finished"),
    ERROR(0x90,"job failed");

    int type;
    String desc;

    JobStatusType(int type, String desc) {
      this.type = type;
      this.desc = desc;
    }

    public static JobStatusType valueOf(Integer code) {
      for (JobStatusType type : JobStatusType.values()) {
        if (type.type == code) {
          return type;
        }
      }
      return null;
    }

    public int getType() {
      return type;
    }

    public String getDesc() {
      return desc;
    }
  }

  }
