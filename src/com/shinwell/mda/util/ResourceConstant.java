/**
 * 
 */
package com.shinwell.mda.util;



/**
 * @Project_name DebugAssistantTool
 * @Author: Shinwell Ku
 * @Date: 2017年7月25日下午1:54:04
 * @Version: 1.0
 * @Desc:
 */
public interface ResourceConstant {
	/**
	 * LANGUAGE
	 */
	public static final String LANGUAGE_LOCAL_ZH_CN = "zh_CN";
	public static final String LANGUAGE_LOCAL_EN_US = "en_US";
	/**
	 * DATA STORAGE
	 */
	public static final String DATA_STORAGE_PATH_SMIS = "conf/smis.dat";
	public static final String DATA_STORAGE_PATH_SSH = "conf/ssh.dat";
	public static final String DATA_STORAGE_PATH_WMI = "conf/wmi.dat";
	public static final String DATA_STORAGE_PATH_SNMP = "conf/snmp.dat";
	/**
	 * IMAGE
	 */
	public static final String IMAGE_APP_LOGO = "image/debug.png";
	public static final String IMAGE_ENTERPRISE_LOGO = "image/logo.png";
	public static final String IMAGE_SMIS_ACTION = "image/smis.png";
	public static final String IMAGE_WMI_ACTION = "image/wmi.gif";
	public static final String IMAGE_SNMP_ACTION = "image/snmp.gif";
	public static final String IMAGE_SSH_ACTION = "image/ssh.png";
	public static final String IMAGE_EXIT_ACTION = "image/exit.png";
	public static final String IMAGE_TREE_ROOT = "image/tree.png";
	public static final String IMAGE_TREE_NODE = "image/treeNode.png";
	public static final String IMAGE_TREE_INSTANCE = "image/subsystem.gif";
	public static final String IMAGE_TREE_FABRIC = "image/fabric.png";
	public static final String IMAGE_ADD_ACTION = "image/add.png";
	public static final String IMAGE_EDIT_ACTION = "image/edit.png";
	public static final String IMAGE_COPY_ACTION = "image/copy.gif";
	public static final String IMAGE_DELETE_ACTION = "image/delete.gif";
	public static final String IMAGE_REMOVE_ACTION = "image/remove.png";
	public static final String IMAGE_SAVE_ACTION = "image/save.png";
	public static final String IMAGE_EXPORT_ACTION = "image/zip.gif";
	public static final String IMAGE_SYS_TRAY = "image/debug.png";
	public static final String IMAGE_SYSTEM_WELCOM = "image/welcome.png";
	public static final String IMAGE_DISCOVER_ACTION = "image/search.png";
	public static final String IMAGE_TEST_ACTION = "image/conn.gif";
	public static final String IMAGE_HELP_ACTION = "image/help.png";
	public static final String IMAGE_OPERATOR_ACTION = "image/operator.gif";
	public static final String IMAGE_VIEW_ACTION = "image/view.png";
	public static final String IMAGE_CONFIG_ACTION = "image/config.png";
	public static final String IMAGE_CUT_ACTION = "image/cut.png";
	public static final String IMAGE_FILE_ACTION = "image/file.png";
	public static final String IMAGE_MGR_ACTION = "image/mgr.gif";
	public static final String IMAGE_TOOLBAR_ACTION = "image/toolbar.gif";
	public static final String IMAGE_LANGUAGE_ACTION = "image/language.png";
	public static final String IMAGE_ABOUT_DIALOG = "image/ercode.jpg";
	public static final String IMAGE_SNAPSHOT_CLEAN = "image/clean.gif";
	public static final String IMAGE_SNAPSHOT_SAVE = "image/save.gif";
	public static final String IMAGE_SNAPSHOT_COPY = "image/copy.gif";
	public static final String IMAGE_SNAPSHOT_SAVES = "image/saves.png";
	public static final String IMAGE_SNMP_TRAPVIEW= "image/trapview.gif";
	public static final String IMAGE_SNMP_SENDTRAP= "image/sendtrap.gif";
	/**
	 * I18N MSG
	 */
	public static final String I18N_KEY_APP_NAME = "APP_NAME";
	public static final String I18N_KEY_APP_VERSION = "APP_VERSION";
	public static final String I18N_KEY_COPY_RIGHT = "COPY_RIGHT";
	public static final String I18N_KEY_WELCOME = "WELCOME";
	public static final String I18N_KEY_SYSTEM_TRAY_MSG = "SYSTEM_TRAY_MSG";
	public static final String I18N_KEY_SMIS_KPI_KEY = "SMIS_KPI_KEY";
	public static final String I18N_KEY_CONFIRM_DELETE_MSG = "CONFIRM_DELETE_MSG";
	public static final String I18N_KEY_CONNECT_SUCESSED = "CONNECT_SUCESSED";
	public static final String I18N_KEY_CONNECT_FAILURE = "CONNECT_FAILURE";
	public static final String I18N_KEY_TEST_FAIL_DETAIL = "TEST_FAIL_DETAIL";
	public static final String I18N_KEY_NAMESPACE = "NAMESPACE";
	public static final String I18N_KEY_ONLINE_UPDATE = "ONLINE_UPDATE";
	public static final String I18N_KEY_SHOW = "SHOW";
	public static final String I18N_KEY_HIDE = "HIDE";
	public static final String I18N_KEY_ABOUT_MSG = "ABOUT_MSG";
	public static final String I18N_KEY_EXIT = "EXIT";
	public static final String I18N_KEY_CONFIRM_EXIT_MSG = "CONFIRM_EXIT_MSG";
	public static final String I18N_KEY_AUTHOR_NAME = "AUTHOR_NAME";
	
	/**
	 *MENU
	 */
	public static final String I18N_KEY_MENU_ITEM_EXIT = "MENU_ITEM_EXIT";
	public static final String I18N_KEY_MENU_ITEM_OPEN = "MENU_ITEM_OPEN";
	public static final String I18N_KEY_MENU_ITEM_SAVE = "MENU_ITEM_SAVE";
	public static final String I18N_KEY_MENU_ITEM_COPY = "MENU_ITEM_COPY";
	public static final String I18N_KEY_MENU_ITEM_CUT = "MENU_ITEM_CUT";
	public static final String I18N_KEY_MENU_ITEM_TOOLBAR = "MENU_ITEM_TOOLBAR";
	public static final String I18N_KEY_MENU_ITEM_LANGUAGE = "MENU_ITEM_LANGUAGE";
	public static final String I18N_KEY_MENU_ITEM_LANGUAGE_ZH = "MENU_ITEM_LANGUAGE_ZH";
	public static final String I18N_KEY_MENU_ITEM_LANGUAGE_EN = "MENU_ITEM_LANGUAGE_EN";
	public static final String I18N_KEY_MENU_ITEM_SMIS = "MENU_ITEM_SMIS";
	public static final String I18N_KEY_MENU_ITEM_WMI = "MENU_ITEM_WMI";
	public static final String I18N_KEY_MENU_ITEM_SNMP = "MENU_ITEM_SNMP";
	public static final String I18N_KEY_MENU_ITEM_SSH = "MENU_ITEM_SSH";
	public static final String I18N_KEY_MENU_ITEM_HELP = "MENU_ITEM_HELP";
	public static final String I18N_KEY_MENU_ITEM_HELP_DOC = "MENU_ITEM_HELP_DOC";
	public static final String I18N_KEY_MENU_ITEM_WELCOME = "MENU_ITEM_WELCOME";
	public static final String I18N_KEY_MENU_ITEM_ABOUT = "MENU_ITEM_ABOUT";
	public static final String I18N_KEY_MENU_ITEM_SMIS_MGR = "MENU_ITEM_SMIS_MGR";
	public static final String I18N_KEY_MENU_ITEM_WMI_MGR = "MENU_ITEM_WMI_MGR";
	public static final String I18N_KEY_MENU_ITEM_SNMP_MGR = "MENU_ITEM_SNMP_MGR";
	public static final String I18N_KEY_MENU_ITEM_SSH_MGR = "MENU_ITEM_SSH_MGR";
	public static final String COMPONET_TYPE_CUSTOM_QUERY = "CUSTOM_QUERY";
	public static final String COMPONET_TYPE_EXPORT = "EXPORT_DATA";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS = "PERFORMANCE_ANALYSIS";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLETIME = "PERFORMANCE_ANALYSIS_SAMPLETIME";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_FREQ = "PERFORMANCE_ANALYSIS_SAMPLE_FREQ";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START = "PERFORMANCE_ANALYSIS_BUTTON_START";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_STOP = "PERFORMANCE_ANALYSIS_BUTTON_STOP";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_MESSAGE = "PERFORMANCE_ANALYSIS_MESSAGE";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_KPI = "PERFORMANCE_ANALYSIS_SAMPLE_KPI";
	public static final String COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_FREQ_UNIT = "PERFORMANCE_ANALYSIS_SAMPLE_FREQ_UNIT";
	
	/**
	 * COMPONET
	 */
	public static final String COMPONET_TYPE_SUBSYSTEM = "SUBSYSTEM";
	public static final String COMPONET_TYPE_CONTROLLER = "CONTROLLER";
	public static final String COMPONET_TYPE_POOL = "POOL";
	public static final String COMPONET_TYPE_DISK = "DISK";
	public static final String COMPONET_TYPE_VOLUME = "VOLUME";
	public static final String COMPONET_TYPE_PORT = "PORT";
	public static final String COMPONET_TYPE_BETTERY = "BETTERY";
	public static final String COMPONET_TYPE_POWERSUPPLY = "POWERSUPPLY";
	public static final String COMPONET_TYPE_FAN = "FAN";
	
	public static final String COMPONET_TYPE_FABRIC = "FABRIC";
	public static final String COMPONET_TYPE_SWITCH = "SWITCH";
	public static final String COMPONET_TYPE_BLADE = "BLADE";
	public static final String COMPONET_TYPE_ZONE = "ZONE";
	public static final String COMPONET_TYPE_ZONEALIAS = "ZONEALIAS";
	public static final String COMPONET_TYPE_ZONE_CONFIG = "ZONE_CONFIG";
	public static final String COMPONET_TYPE_SWITCH_MODULE = "SWITCH_MODULE";
	public static final String COMPONET_TYPE_SWITCH_PORT = "SWITCH_PORT";
	public static final String COMPONET_TYPE_SWITCH_POWERSUPPLY = "SWITCH_POWERSUPPLY";
	public static final String COMPONET_TYPE_SWITCH_FAN = "SWITCH_FAN";
	public static final String COMPONET_TYPE_REMOTE_DEVICE = "REMOTE_DEVICE";
	
	public static final String COMPONET_TYPE_TAPE_LIBRARY = "TAPE_LIBRARY";
	public static final String COMPONET_TYPE_TAPE_DRIVER = "TAPE_DRIVER";
	public static final String COMPONET_TYPE_TAPE_CHANGER = "TAPE_CHANGER";
	public static final String COMPONET_TYPE_TAPE_PORT = "TAPE_PORT";
	public static final String COMPONET_TYPE_TAPE_MEADIA = "TAPE_MEADIA";
	

	/**
	 * TOOLITEM
	 */
	public static final String COMPONET_TYPE_TOOLITEM_ADD = "TOOLITEM_ADD";
	public static final String COMPONET_TYPE_TOOLITEM_EDIT = "TOOLITEM_EDIT";
	public static final String COMPONET_TYPE_TOOLITEM_DELETE = "TOOLITEM_DELETE";
	public static final String COMPONET_TYPE_TOOLITEM_CONNECT = "TOOLITEM_CONNECT";
	public static final String COMPONET_TYPE_TOOLITEM_DISCOVER = "TOOLITEM_DISCOVER";
	public static final String COMPONET_TYPE_TOOLITEM_COPY = "TOOLITEM_COPY";
	public static final String COMPONET_TYPE_TOOLITEM_PASTE = "TOOLITEM_PASTE";
	public static final String COMPONET_TYPE_TOOLITEM_TRAP_TEST = "TOOLITEM_TRAP_TEST";
	public static final String COMPONET_TYPE_TOOLITEM_TRAP_RECEIVE = "TOOLITEM_TRAP_RECEIVE";
	
	/**
	 * MSG CONSTANTS
	 */
	public static final String I18N_KEY_MSG_INFO = "MSG_INFO";
	public static final String I18N_KEY_MSG_ERROR = "MSG_ERROR";
	public static final String I18N_KEY_MSG_WARNING = "MSG_WARNING";
	public static final String I18N_KEY_MSG_CONFIRM = "MSG_CONFIRM";
	public static final String I18N_KEY_MSG_QUEST = "MSG_QUEST";
	public static final String I18N_KEY_MSG_UNDO = "MSG_UNDO";
	public static final String I18N_KEY_MSG_NO_SELECT_DATA = "NO_SELECT_DATA";
	public static final String I18N_KEY_MSG_EXPORT = "MSG_EXPORT";
	public static final String I18N_KEY_MSG_NO_EMPTY = "MSG_NO_EMPTY";
	public static final String I18N_KEY_INSTANCE_NAME = "INSTANCE_NAME";
	public static final String I18N_KEY_INDEX = "INDEX";
	public static final String I18N_KEY_NO_DATA = "NO_DATA";
	public static final String I18N_KEY_CONFIG_KEY = "CONFIG_KEY";
	public static final String I18N_KEY_CONFIG_KEY_VALUE = "CONFIG_KEY_VALUE";
	public static final String I18N_KEY_CONFIG_KEY_DATATYPE = "CONFIG_KEY_DATATYPE";
	public static final String I18N_KEY_PERF_KEY = "PERF_KEY";
	public static final String I18N_KEY_PERF_KEY_VALUE = "PERF_KEY_VALUE";
	public static final String I18N_KEY_PERF_KEY_DATATYPE = "PERF_KEY_DATATYPE";
	public static final String I18N_KEY_DOUBLE_SEE_DATAIL = "DOUBLE_SEE_DATAIL";
	public static final String I18N_KEY_VERSION_LABEL = "VERSION_LABEL";
	public static final String I18N_KEY_CLIENT_OS_NAME = "CLIENT_OS_NAME";
	public static final String I18N_KEY_CLIENT_OS_VERSION = "CLIENT_OS_VERSION";
	public static final String I18N_KEY_CLIENT_OS_ARCH = "CLIENT_OS_ARCH";
	public static final String I18N_KEY_CLIENT_JAVA_RUNTIME_VERSION = "CLIENT_JAVA_RUNTIME_VERSION";
	public static final String I18N_KEY_CLIENT_COPY_RIGHT_LABEL = "COPY_RIGHT_LABEL";
	public static final String I18N_KEY_CLIENT_EMAIL_LABEL = "CLIENT_EMAIL_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_TITLE = "SMIS_DIALOG_TITLE";
	public static final String I18N_KEY_SMIS_DIALOG_TIP_MSG = "SMIS_DIALOG_TIP_MSG";
	public static final String I18N_KEY_SMIS_DIALOG_DISPLAY_NAME_LABEL = "SMIS_DIALOG_DISPLAY_NAME_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_PROTOCOL_LABEL = "SMIS_DIALOG_PROTOCOL_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_HOST_LABEL = "SMIS_DIALOG_HOST_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_PORT_LABEL = "SMIS_DIALOG_PORT_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_NAMESPACE_LABEL = "SMIS_DIALOG_NAMESPACE_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_USER_LABEL = "SMIS_DIALOG_USER_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_PASSWORD_LABEL = "SMIS_DIALOG_PASSWORD_LABEL";
	public static final String I18N_KEY_SMIS_DIALOG_SAVE_DEFAULT_LABEL = "SMIS_DIALOG_SAVE_DEFAULT_LABEL";
	public static final String I18N_KEY_MENU_ITEM_FILE = "MENU_ITEM_FILE";
	public static final String I18N_KEY_MENU_ITEM_EDIT = "MENU_ITEM_EDIT";
	public static final String I18N_KEY_MENU_ITEM_VIEW = "MENU_ITEM_VIEW";
	public static final String I18N_KEY_MENU_ITEM_CONFIG = "MENU_ITEM_CONFIG";
	public static final String I18N_KEY_MENU_ITEM_OPERATOR = "MENU_ITEM_OPERATOR";
	
	public static final String I18N_KEY_UI_QUERY_TIP = "UI_QUERY_TIP";
	public static final String I18N_KEY_UI_CONNECT_TIP = "UI_CONNECT_TIP";
	public static final String I18N_KEY_UI_DISCOVER_TIP = "UI_DISCOVER_TIP";
	public static final String I18N_KEY_DEFINE_CLASS = "DEFINE_CLASS";
	public static final String I18N_KEY_ASSOCIATOR_CLASS = "ASSOCIATOR_CLASS";
	public static final String I18N_KEY_RESULT_CLASS = "RESULT_CLASS";
	public static final String I18N_KEY_UI_EXPORT_DIALOG_TITLE = "EXPORT_DIALOG_TITLE";
	
	//SNMP
	public static final String I18N_KEY_SNMP_DIALOG_EOID_LABEL = "SNMP_DIALOG_EOID_LABEL";
	public static final String I18N_KEY_SNMP_DIALOG_READ_COMMUNITY_LABEL = "SNMP_DIALOG_READ_COMMUNITY_LABE";
	public static final String I18N_KEY_SNMP_DIALOG_WRITE_COMMUNITY_LABEL = "SNMP_DIALOG_WRITE_COMMUNITY_LABEL";
	public static final String I18N_KEY_SNMP_DIALOG_HOST_LABEL = "SNMP_DIALOG_HOST_LABEL";
	public static final String I18N_KEY_SNMP_DIALOG_PORT_LABEL = "SNMP_DIALOG_PORT_LABEL";
	public static final String I18N_KEY_SNMP_DIALOG_TITLE = "SNMP_DIALOG_TITLE";
	public static final String I18N_KEY_SNMP_DIALOG_TIP_MSG = "SNMP_DIALOG_TIP_MSG";
	
	//UI
	public static final String I18N_KEY_SNMP_TRAP_SENDER_CONFIG = "SNMP_TRAP_SENDER_CONFIG";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SERVER_IP = "SNMP_TRAP_SENDER_SERVER_IP";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SERVER_PORT = "SNMP_TRAP_SENDER_SERVER_PORT";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SERVER_TYPE = "SNMP_TRAP_SENDER_SERVER_TYPE";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SERVER_VERSION = "SNMP_TRAP_SENDER_SERVER_VERSION";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SERVER_RETRY_TIMES = "SNMP_TRAP_SENDER_SERVER_RETRY_TIMES";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_TIMEOUT = "SNMP_TRAP_SENDER_TIMEOUT";
	public static final String I18N_KEY_SNMP_TRAP_COMMUNITY = "SNMP_TRAP_COMMUNITY";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_BUTTON = "SNMP_TRAP_SENDER_BUTTON";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_CUSTOM_OID = "SNMP_TRAP_SENDER_CUSTOM_OID";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_ADD_TABLE_OID = "SNMP_TRAP_SENDER_ADD_TABLE_OID";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_EDIT_TABLE_OID = "SNMP_TRAP_SENDER_EDIT_TABLE_OID";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_DELETE_TABLE_OID = "SNMP_TRAP_SENDER_DELETE_TABLE_OID";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_TABLE_OID = "SNMP_TRAP_SENDER_TABLE_OID";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_TABLE_VALUE = "SNMP_TRAP_SENDER_TABLE_VALUE";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_TABLE_NO_OID_ITEM = "SNMP_TRAP_SENDER_TABLE_NO_OID_ITEM";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_TRAP_CONTENT_NOT_EMPTY = "SNMP_TRAP_SENDER_TRAP_CONTENT_NOT_EMPTY";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SENT_SUCCESFUL = "SNMP_TRAP_SENDER_SENT_SUCCESFUL";
	public static final String I18N_KEY_SNMP_TRAP_SENDER_SENT_FAIL = "SNMP_TRAP_SENDER_SENT_FAIL";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_CONFIG = "SNMP_TRAP_RECEIVER_CONFIG";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_LISTENER_PORT = "SNMP_TRAP_RECEIVER_LISTENER_PORT";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_START = "SNMP_TRAP_RECEIVER_START";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_STOP = "SNMP_TRAP_RECEIVER_STOP";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_DELETE = "SNMP_TRAP_RECEIVER_DELETE";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_DETAIL = "SNMP_TRAP_RECEIVER_TABLE_DETAIL";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_TYPE = "SNMP_TRAP_RECEIVER_TABLE_COLUMN_TYPE";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_SOURCE = "SNMP_TRAP_RECEIVER_TABLE_COLUMN_SOURCE";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_DATE = "SNMP_TRAP_RECEIVER_TABLE_COLUMN_DATE";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_TABLE_COLUMN_MSG = "SNMP_TRAP_RECEIVER_TABLE_COLUMN_MSG";
	public static final String I18N_KEY_SNMP_TRAP_RECEIVER_ORIGINAL_TRAP = "SNMP_TRAP_RECEIVER_ORIGINAL_TRAP";
	public static final String I18N_KEY_SNMP_QUERY_EXECUTE_BUTTON = "SNMP_QUERY_EXECUTE_BUTTON";
	public static final String I18N_KEY_SNMP_QUERY_RESET_BUTTON = "SNMP_QUERY_RESET_BUTTON";
	public static final String I18N_KEY_SNMP_QUERY_CLEAR_BUTTON = "SNMP_QUERY_CLEAR_BUTTON";
	public static final String I18N_KEY_SNMP_QUERY_EXECUTE_RESULT = "SNMP_QUERY_EXECUTE_RESULT";
	public static final String I18N_KEY_SNMP_QUERY_HOST_LABEL = "SNMP_QUERY_HOST_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_PORT_LABEL = "SNMP_QUERY_PORT_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_SNMPVERSION_LABEL = "SNMP_QUERY_SNMPVERSION_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_OPERATOR_LABEL = "SNMP_QUERY_OPERATOR_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_R_COMMUNITY_LABEL = "SNMP_QUERY_R_COMMUNITY_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_W_COMMUNITY_LABEL = "SNMP_QUERY_W_COMMUNITY_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_OID_LABEL = "SNMP_QUERY_OID_LABEL";
	public static final String I18N_KEY_SNMP_QUERY_VALUE_LABEL = "SNMP_QUERY_VALUE_LABEL";
	
	
}
