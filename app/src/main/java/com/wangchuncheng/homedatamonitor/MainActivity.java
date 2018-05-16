package com.wangchuncheng.homedatamonitor;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wangchuncheng.homedatamonitor.entity.HomeData;
import com.wangchuncheng.homedatamonitor.service.MqttClientService;
import com.wangchuncheng.homedatamonitor.utils.MqttMessageHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    MqttClientService mMqttClientService;// = MqttClientService.getMqttService();

    private LineChartView lineChart;
    private List<PointValue> mTemperatureValues;// = new ArrayList<PointValue>();
    private List<PointValue> mHumidityPercentages;// = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues;// = new ArrayList<AxisValue>();
    private List<HomeData> mHomeDataList = new ArrayList<HomeData>();
//    final String[] spinnerStrings = new String[]{"温度", "湿度", "温度和湿度"};
//    String selectInfo = "温度";

    private EditText mEditText;
    private FloatingActionButton mRefreshButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MqttMessageHandler.getHandler().initHandler(this);
        mMqttClientService = new MqttClientService(getApplicationContext());

        setContentView(R.layout.activity_hello_charts);
        lineChart = findViewById(R.id.line_chart_view);
        initHomeData("101");
        initLineChart(2);//初始化

        mEditText = findViewById(R.id.get_homeId);
        mRefreshButton = findViewById(R.id.fab);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            final String[] preHomeIds = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
            final String[] sufHomeIds = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            @Override
            public void onClick(View v) {
                String roomId = String.valueOf(mEditText.getText());//return char sequence
                //check if roomId is valid or not
                boolean isValid = false;
                for (int i = 0; i < preHomeIds.length; i++) {
                    for (int j = 0; j < sufHomeIds.length; j++) {
                        if ((preHomeIds[i] + sufHomeIds[j]).equals(roomId)) {
                            isValid = true;
                            break;
                        }
                    }
                }
                if (isValid) {
                    mMqttClientService.publishRequestData(roomId); //valid ,send request to server
                } else {
                    //invalid,
                    Toast.makeText(MainActivity.this, "房间号输入有误！请重新输入", Toast.LENGTH_SHORT).show();
                    mEditText.requestFocus();
                }
            }
        });
    }

    private void initHomeData(String homeID) {
        long timePoint = new Date().getTime();
//        System.out.println(timePoint);
//        HomeData temp = new HomeData(homeID, 0, 0, timePoint);
        for (int i = 0; i < 20; i++) {
            double temperature = 25 + (Math.random() * 10 - 5);
            double humidity = 50 + (Math.random() * 60 - 30);
            HomeData temp = new HomeData("101", temperature, humidity, timePoint + i * 1000);
            mHomeDataList.add(temp);
        }
    }

    private void initLineChart(int opt) {
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点

        List<Line> lines = new ArrayList<Line>();
        //humidity Line
        Line humidityLine = new Line(mHumidityPercentages).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        humidityLine.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        humidityLine.setCubic(false);//曲线是否平滑，即是曲线还是折线
        humidityLine.setFilled(false);//是否填充曲线的面积
        humidityLine.setHasLabels(true);//曲线的数据坐标是否加上备注
//      temperatureLine.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        humidityLine.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        humidityLine.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        if (opt == 1 || opt == 2)
            lines.add(humidityLine);
        //temperature Line
        Line temperatureLine = new Line(mTemperatureValues).setColor(Color.parseColor("#ADFF2F"));  //折线的颜色（浅绿）
        temperatureLine.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        temperatureLine.setCubic(false);//曲线是否平滑，即是曲线还是折线
        temperatureLine.setFilled(false);//是否填充曲线的面积
        temperatureLine.setHasLabels(true);//曲线的数据坐标是否加上备注
        temperatureLine.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        temperatureLine.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        if (opt == 0 || opt == 2)
            lines.add(temperatureLine);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.LTGRAY);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(10); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }

    private void getAxisPoints() {
        mTemperatureValues = new ArrayList<>();
        mHumidityPercentages = new ArrayList<>();
        for (int i = 0; i < mHomeDataList.size(); i++) {
            mTemperatureValues.add(new PointValue(i, (float) mHomeDataList.get(i).getTemperature()));
            mHumidityPercentages.add(new PointValue(i, (float) mHomeDataList.get(i).getHumidityPercentage()));
//            mBrightnessPercentages.add(new PointValue(i, (float) mHomeDataList.get(i).getBrightnessPercentage()));
        }
    }

    private void getAxisXLables() {
        mAxisXValues = new ArrayList<>();
        for (int i = 0; i < mHomeDataList.size(); i++) {
            Timestamp timestamp = new Timestamp(mHomeDataList.get(i).getPointtime());
            mAxisXValues.add(new AxisValue(i).setLabel(timestamp.toString()));
        }
    }

    public void validate(HomeData data) {
        mHomeDataList.add(data);
        mHomeDataList.remove(0);
        initLineChart(2);
    }
}
