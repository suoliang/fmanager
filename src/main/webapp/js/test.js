option = {
	tooltip : {
		trigger : 'item',
		formatter : "{b}:{c}",
		x : 'right',
		y : 'top'
	},
	legend : {
		data : [ 'test1', 'test2', 'test3', 'test4', 'test5' ],
		x : 2000,
		y : 2000
	},
	calculable : true,
	color : [ '#2D697B', '#3F8EA3', '#46a1b9', '#7cbbcf', '#b5d4e0', '#BECED5', '#DBE5E6' ],
	series : [ {
		type : 'pie',
		radius : [ '50%', '80%' ],
		center : [ '50%', '55%' ],
		itemStyle : {
			normal : {
				label : {
					position : 'inner',
					formatter : function(a, b, c, d) {
						return (d - 0).toFixed(0) + '%'
					}
				},
				labelLine : {
					show : false
				}
			},
			emphasis : {
				label : {
					show : true,
					formatter : "{d}%",
					textStyle : {
						fontSize : '10',
						fontWeight : 'bold',
						color : '#000033'
					}
				}
			},
			borderWidth : 0
		},
		data : [ {
			name : 'test1',
			value : 1
		}, {
			name : 'test2',
			value : 2
		}, {
			name : 'test3',
			value : 3
		}, {
			name : 'test4',
			value : 4
		}, {
			name : 'test5',
			value : 5
		} ]
	} ]
}