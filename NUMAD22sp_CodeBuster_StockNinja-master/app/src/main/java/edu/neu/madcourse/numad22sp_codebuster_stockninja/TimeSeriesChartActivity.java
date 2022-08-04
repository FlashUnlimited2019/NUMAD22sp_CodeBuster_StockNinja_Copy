package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.TimeSeries;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesChartActivity extends AppCompatActivity {

  private static final String MSG_ROTATE_SCREEN = "Please rotate your device to landscape for better experience.";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_time_series_chart);

    Bundle extras = super.getIntent().getExtras();
    if (extras == null || extras.getString("stockSymbol") == null
        || extras.getParcelableArrayList("timeSeries") == null) {
      Intent intent = new Intent(this, SearchSymbolActivity.class);
      super.startActivity(intent);
      return;
    }

    if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      Toast.makeText(this, MSG_ROTATE_SCREEN, Toast.LENGTH_LONG).show();
    }

    String stockSymbol = extras.getString("stockSymbol");
    List<TimeSeries> timeSeries = extras.getParcelableArrayList("timeSeries");

    AnyChartView vTimeSeriesChart = findViewById(R.id.vTimeSeriesChart);
    vTimeSeriesChart.setProgressBar(findViewById(R.id.vChartStatus));

    Table table = Table.instantiate("x");
    table.addData(this.generateDataEntries(timeSeries));

    TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close'}");

    Stock stock = AnyChart.stock();

    Plot plot = stock.plot(0);
    plot.yGrid(true)
        .xGrid(true)
        .yMinorGrid(true)
        .xMinorGrid(true);

    plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

    plot.ohlc(mapping)
        .name(stockSymbol)
        .legendItem("{\n" +
            "        iconType: 'rising-falling'\n" +
            "      }");

    stock.scroller().ohlc(mapping);
    vTimeSeriesChart.setChart(stock);
  }

  private List<DataEntry> generateDataEntries(List<TimeSeries> timeSeries) {
    List<DataEntry> dataEntries = new ArrayList<>();
    for (int i = timeSeries.size() - 1; i >= 0; i--) {
      dataEntries.add(new TimeSeriesDataEntry(timeSeries.get(i)));
    }
    return dataEntries;
  }

  private static class TimeSeriesDataEntry extends HighLowDataEntry {

    private TimeSeriesDataEntry(TimeSeries ts) {
      super(generateLongTime(ts.getTimestamp()), ts.getHigh(), ts.getLow());
      setValue("open", ts.getOpen());
      setValue("close", ts.getClose());
    }

    private static Long generateLongTime(String timestamp) {
      return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
          .atZone(ZoneId.of("America/New_York")).toInstant().toEpochMilli();
    }
  }
}