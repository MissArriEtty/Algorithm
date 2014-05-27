package monitor;

public class Commands {
	public static final int REGISTER = 0;//down->mon
	public static final int CANCEL_JOB = 1; //mon->down;
	public static final int FAIL_JOB = 2; //down->mon
	public static final int FINISH_JOB = 3; //down->mon
	public static final int SHUTDOWN_SERVICE = 4;//m->d
	public static final int RESTART_SERVICE = 5;//md
	public static final int SHUTDOWN_NODE  = 6;//md
	public static final int RESTART_NODE = 7;//md
	public static final int REREGISTER = 8;//md
	public static final int CACHE_FILE =9;//md
	public static final int HEART_BEAT_REPORT = 10;//dm
	
	public static final int REGISTER_OK = 80;//md
	public static final int JOB_STARTED = 81;//dm
	public static final int JOB_CANCELED = 82;//dm
	public static final int JOB_FINISHED = 83;//dm
	public static final int JOB_FAILED = 84;//dm
	public static final int FILE_CACHED = 85;//dm
	
	public static final int GOOD_GO = 100;
	public static final int ILLEGAL_FLAG = -1;
}
