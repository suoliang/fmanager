package com.cyyun.fm.analyze.bean;

import java.util.List;

/**
 * <h3>饼图数据</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class LineChartDataView {
	private List<String> legendName;
	private List<String> axisName;
	private List<List<Long>> seriesDatas;

	public List<String> getLegendName() {
		return legendName;
	}

	public void setLegendName(List<String> legendName) {
		this.legendName = legendName;
	}

	public List<String> getAxisName() {
		return axisName;
	}

	public void setAxisName(List<String> axisName) {
		this.axisName = axisName;
	}

	public List<List<Long>> getSeriesDatas() {
		return seriesDatas;
	}

	public void setSeriesDatas(List<List<Long>> seriesDatas) {
		this.seriesDatas = seriesDatas;
	}
}