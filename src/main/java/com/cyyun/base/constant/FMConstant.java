package com.cyyun.base.constant;

/**
 * <h3>FM 常量</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class FMConstant {
	/**
	 * 接口类型：文章阶段
	 */
	public static final String ARTICLE_STAGE = ArticleConstants.ArticleStage.PUTONG;
	/**
	 * 默认当前页
	 */
	public static final Integer PAGE_NO = 1;
	/**
	 * 默认分页size
	 */
	public static final Integer PAGE_SIZE = 10;
	/**
	 * 个人信息->原始密码错误提示
	 */
	public static final String PASSWORD_ERROR = "原始密码错误";
	/**
	 * 保存组织->保存失败提示
	 */
	public static final String ORGANIZATION_ERROR = "新增组织失败";
	/**
	 * 系统组织管理父组织ID
	 */
	public static final String parentIdVal = "0";
	/**
	 * 系统查询组织失败提示
	 */
	public static final String QUERY_ORGANIZATION_FAILURE = "查询组织失败";
	/**
	 * 文章作者无时页面显示
	 */
	public static final String ARTICLEVO_AUTHOR_SHOW = "未知";
	/**
	 * 文章所属地域无时页面显示
	 */
	public static final String ARTICLEVO_AREA_SHOW = "未知";
	/**
	 * 文章所属行业无时页面显示
	 */
	public static final String ARTICLEVO_INDUSTRY_SHOW = "未知";
	/**
	 * 文章正负面页面显示
	 */
	public static final String ARTICLEVO_SENTIMENT_SHOW = "未知";
	/**
	 * 面板跳转标识
	 */
	public static final String FROM_BOARD = "Y";
	/**
	 * 日期格式化格式
	 */
	public static final String DATE_FOMAT_YYYYMMDD = "yyyy-MM-dd";

	/**
	 * 日期格式化格式
	 */
	public static final String DATE_FOMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

	public static final Integer CUST_TOPIC_FENBU_LIMIT = 50;

	public static final Integer CUST_TOPIC_DAILY_TOP = 10;

	/**
	 * 专题状态
	 * @author cyyun
	 *
	 */
	public enum CUST_TOPIC_STATUS {
		yes, no;
		public Integer getStatus() {
			switch (this) {
			case yes:
				return 1;
			case no:
				return 0;
			default:
				return null;
			}
		}
	}

	/**
	 *  返回文章列表的类型 缺省为文章，1为回帖，2为查询所有类型
	 * @author cyyun
	 */
	public enum ARTICLEOREPLY_FLAG {
		ONE, TWO;
		public String getValue() {
			switch (this) {
			case ONE:
				return "1";
			case TWO:
				return "2";
			default:
				return null;
			}
		}
	}

	/**
	 * 专题显示
	 */
	public enum TOPIC_DISPLAY {
		none;
		public String getValue() {
			switch (this) {
			case none:
				return "暂无";
			default:
				return null;
			}
		}
	}

	/**
	 * 预警显示
	 */
	public enum FOCUS_DISPLAY {
		none, noneauto;
		public String getValue() {
			switch (this) {
			case none:
				return "人工";
			case noneauto:
				return "自动";
			default:
				return null;
			}
		}
	}

	public enum CUST_TOPIC_IS_ENABLE {
		enable, disable;
		public Integer getValue() {
			switch (this) {
			case enable:
				return 1;
			case disable:
				return 0;
			default:
				return null;
			}
		}
	}

	/**
	 * 区分分类页面标识
	 * @author LIUJUNWU
	 */
	public enum CLASSIFICATIOTYPE {
		event, person, site;
		public Integer getValue() {
			switch (this) {
			case event:
				return 1;
			case person:
				return 2;
			case site:
				return 3;
			default:
				return null;
			}
		}

		public String getTitle() {
			switch (this) {
			case event:
				return "事件分类";
			case person:
				return "人物分类";
			case site:
				return "站点分类";
			default:
				return null;
			}
		}
	}

	/**
	 * 分类页面名称
	 * @author LIUJUNWU
	 */
	public enum CLASSIFICATIONAME {
		event, person, site;
		public String getValue() {
			switch (this) {
			case event:
				return "事件分类";
			case person:
				return "人物分类";
			case site:
				return "站点分类";
			default:
				return null;
			}
		}
	}

	/**
	 * 文章正负面
	 * @author yaodw
	 *
	 */
	public enum ARTICLE_SENTIMENT {
		zero, one, two, three;
		public String getValue() {
			switch (this) {
			case zero:
				return FMConstant.ARTICLEVO_SENTIMENT_SHOW;
			case one:
				return "负面";
			case two:
				return "正面";
			case three:
				return "中性";
			default:
				return FMConstant.ARTICLEVO_SENTIMENT_SHOW;
			}
		}
	}
}