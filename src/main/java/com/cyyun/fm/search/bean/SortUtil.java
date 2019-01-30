package com.cyyun.fm.search.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortUtil {

	static Map<String, Integer> sortNameMap = new HashMap<String, Integer>();
	static {
		sortNameMap.put("张三", 1);
		sortNameMap.put("王五", 2);
		sortNameMap.put("赵六", 3);
		sortNameMap.put("李四", 4);
	}

	public static Integer getSortNameMapByKey(String key) {
		for (String s : sortNameMap.keySet()) {
			if (key.contains(s)) {
				return sortNameMap.get(s);
			}
		}
		return sortNameMap.size()+1;
	}

	public static <T> void sort(List<T> list,
			final List<Comparator<T>> comparatorList) {
		if (comparatorList.isEmpty()) {// Always equals, if no Comparator.
			throw new IllegalArgumentException("comparatorList is empty.");
		}
		Comparator<T> comparator = new Comparator<T>() {
			public int compare(T o1, T o2) {
				for (Comparator<T> c : comparatorList) {
					if (c.compare(o1, o2) > 0) {
						return 1;
					} else if (c.compare(o1, o2) < 0) {
						return -1;
					}
				}
				return 0;
			}
		};
		Collections.sort(list, comparator);
	}

	public static <E> void sortByMethod(List<E> list, final String method,
			final boolean reverseFlag) {
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object arg1, Object arg2) {
				int result = 0;
				try {
					Method m1 = ((E) arg1).getClass().getMethod(method, null);
					Method m2 = ((E) arg2).getClass().getMethod(method, null);
					Object obj1 = m1.invoke(((E) arg1), null);
					Object obj2 = m2.invoke(((E) arg2), null);
					if (obj1 instanceof String) {
						if (method.toLowerCase().contains("name")) {
							Integer sort1 = getSortNameMapByKey((String) (obj1));
							Integer sort2 = getSortNameMapByKey((String) (obj2));
							result = sort1.compareTo(sort2);
						} else {
							// 字符串
							result = obj1.toString().compareTo(obj2.toString());
						}
					} else if (obj1 instanceof Date) {
						// 日期
						long l = ((Date) obj1).getTime()
								- ((Date) obj2).getTime();
						if (l > 0) {
							result = 1;
						} else if (l < 0) {
							result = -1;
						} else {
							result = 0;
						}
					} else if (obj1 instanceof Integer) {
						// 整型（Method的返回参数可以是int的，因为JDK1.5之后，Integer与int可以自动转换了）
						result = (Integer) obj1 - (Integer) obj2;
					} else {
						// 目前尚不支持的对象，直接转换为String，然后比较，后果未知
						result = obj1.toString().compareTo(obj2.toString());

						System.err
								.println("MySortList.sortByMethod方法接受到不可识别的对象类型，转换为字符串后比较返回...");
					}

					if (reverseFlag) {
						// 倒序
						result = -result;
					}
				} catch (NoSuchMethodException nsme) {
					nsme.printStackTrace();
				} catch (IllegalAccessException iae) {
					iae.printStackTrace();
				} catch (InvocationTargetException ite) {
					ite.printStackTrace();
				}

				return result;
			}
		});
	}
	
	 public static <E>  void sortByMethod2(List<E> list, final String method,
	            final boolean reverseFlag) {
	        Collections.sort(list, new Comparator<Object>() {
	            @SuppressWarnings("unchecked")
	            public int compare(Object arg1, Object arg2) {
	                int result = 0;
	                try {
	                    Method m1 = ((E) arg1).getClass().getMethod(method, null);
	                    Method m2 = ((E) arg2).getClass().getMethod(method, null);
	                    Object obj1 = m1.invoke(((E)arg1), null);
	                    Object obj2 = m2.invoke(((E)arg2), null);
	                    if(obj1 instanceof String) {
	                        // 字符串
	                        result = obj1.toString().compareTo(obj2.toString());
	                    }else if(obj1 instanceof Date) {
	                        // 日期
	                        long l = ((Date)obj1).getTime() - ((Date)obj2).getTime();
	                        if(l > 0) {
	                            result = 1;
	                        }else if(l < 0) {
	                            result = -1;
	                        }else {
	                            result = 0;
	                        }
	                    }else if(obj1 instanceof Integer) {
	                        // 整型（Method的返回参数可以是int的，因为JDK1.5之后，Integer与int可以自动转换了）
	                        result = (Integer)obj1 - (Integer)obj2;
	                    }else {
	                        // 目前尚不支持的对象，直接转换为String，然后比较，后果未知
	                        result = obj1.toString().compareTo(obj2.toString());
	                        
	                        System.err.println("MySortList.sortByMethod方法接受到不可识别的对象类型，转换为字符串后比较返回...");
	                    }
	                    
	                    if (reverseFlag) {
	                        // 倒序
	                        result = -result;
	                    }
	                } catch (NoSuchMethodException nsme) {
	                    nsme.printStackTrace();
	                } catch (IllegalAccessException iae) {
	                    iae.printStackTrace();
	                } catch (InvocationTargetException ite) {
	                    ite.printStackTrace();
	                }

	                return result;
	            }
	        });
	    }

	public static void main(String[] args) {
		System.out.println("2".compareTo("1"));
		List<Person> list = new ArrayList<Person>();
		Person p = new Person();
		p.setId(1);
		p.setName("张三111");
		list.add(p);
		p = new Person();
		p.setId(2);
		p.setName("李四111");
		list.add(p);
		p = new Person();
		p.setId(3);
		p.setName("啊王五111");
		list.add(p);
		p = new Person();
		p.setId(4);
		p.setName("赵六1111");
		list.add(p);
		p = new Person();
		p.setId(5);
		p.setName("小王1111");
		list.add(p);
		p = new Person();
		p.setId(6);
		p.setName("老王1111");
		list.add(p);

		sortByMethod2(list, "getName", false);
		System.out.println("中文名称正序排列：");
		for (Person pp : list) {
			System.out.println(pp.getId() + "," + pp.getName());
		}
	}

}

class Person {
	private int id;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}