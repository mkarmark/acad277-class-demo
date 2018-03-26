package itp341.karmarkar.mitali.a5;

//Mitali Karmarkar
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //all the widget objects that need to be referenced are declared
    private EditText editTextBillAmount;
    private RadioGroup radioGroupTipBy;
    private TextView textViewPercent;
    private RelativeLayout relativeLayoutPercent;
    private TextView textViewPercentValue;
    private SeekBar seekBarPercentValue;
    private TextView textViewTipValue;
    private EditText editTextTipValue;
    private TextView textViewTotalValue;
    private Spinner spinnerSplitBill;
    private RelativeLayout relativeLayoutPerPerson;
    private TextView textViewPerPersonValue;
    private CheckBox checkBoxRoundUp;

    //primitive types that keep track of the bill, the tip, and the total (and per person values)
    public double billAmount = 0;
    public double tip = 0;
    public int tipPercent = 15;
    public double total = 0;
    public int numPeople = 1;
    public double perPersonTotal = 0;
    public boolean isTipByPercent = true;
    public boolean isRoundingUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //all the widgets are initialized with the correct widget from the xml file
        editTextBillAmount = (EditText) findViewById(R.id.edit_text_bill_amount);
        radioGroupTipBy = (RadioGroup) findViewById(R.id.radio_group_tip_by);
        textViewPercent = (TextView) findViewById(R.id.text_view_percent);
        relativeLayoutPercent = (RelativeLayout) findViewById(R.id.relative_layout_percent_value);
        textViewPercentValue = (TextView) findViewById(R.id.text_view_percent_value);
        seekBarPercentValue = (SeekBar) findViewById(R.id.seekbar_percent);
        textViewTipValue = (TextView) findViewById(R.id.text_view_tip_value);
        editTextTipValue = (EditText) findViewById(R.id.edit_text_tip_value);
        textViewTotalValue = (TextView) findViewById(R.id.text_view_total_value);
        spinnerSplitBill = (Spinner) findViewById(R.id.spinner_split_bill);
        relativeLayoutPerPerson = (RelativeLayout) findViewById(R.id.relative_layout_per_person);
        textViewPerPersonValue = (TextView) findViewById(R.id.text_view_per_person_value);
        checkBoxRoundUp = (CheckBox) findViewById(R.id.checkbox_round_up_to_nearest_dollar);

        //listener updates the bill amount and recalculates tip and total
        editTextBillAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                billAmount = Double.parseDouble(textView.getText().toString());
                updateTipAndTotal();
                return true;
            }
        });

        //listener checks whether to tip by percent or by value and updates the UI accordingly
        radioGroupTipBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.tip_by_percent) {
                    isTipByPercent = true;
                    textViewPercent.setVisibility(View.VISIBLE);
                    relativeLayoutPercent.setVisibility(View.VISIBLE);
                    textViewTipValue.setVisibility(View.VISIBLE);
                    editTextTipValue.setVisibility(View.GONE);
                    textViewPercentValue.setText(getResources().getString(R.string.string_default_percent_value));
                    seekBarPercentValue.setProgress(15);
                    updateTipAndTotal();
                } else {
                    isTipByPercent = false;
                    textViewPercent.setVisibility(View.GONE);
                    relativeLayoutPercent.setVisibility(View.GONE);
                    textViewTipValue.setVisibility(View.GONE);
                    editTextTipValue.setVisibility(View.VISIBLE);
                    editTextTipValue.setText(String.format(getResources().getString(R.string.string_money_value), tip));
                }
            }
        });

        //listener reads in the tip value and updates the total
        editTextTipValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (textView.getText().toString().startsWith("$")) tip = Double.parseDouble(textView.getText().toString().substring(1));
                else tip = Double.parseDouble(textView.getText().toString());
                updateTipAndTotal();
                return true;
            }
        });

        //listener reads what percent tip to give and calculates the tip and total
        seekBarPercentValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tipPercent = i;
                textViewPercentValue.setText(String.format(getResources().getString(R.string.string_percent_value), tipPercent));
                updateTipAndTotal();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //spinner listener gets how many people to split the bill by and updates the per person amount
        spinnerSplitBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected = adapterView.getItemAtPosition(i).toString();
                if (itemSelected == null) return;
                if (itemSelected.equals("No")) {
                    relativeLayoutPerPerson.setVisibility(View.GONE);
                    numPeople = 1;
                } else {
                    relativeLayoutPerPerson.setVisibility(View.VISIBLE);
                    if (itemSelected.equals("2 ways")) {
                        numPeople = 2;
                    } else if (itemSelected.equals("3 ways")) {
                        numPeople = 3;
                    } else {
                        numPeople = 4;
                    }
                }
                updateTipAndTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //listener sees if user wants to round up to the nearest dollar and updates
        //tip and total accordingly
        checkBoxRoundUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isRoundingUp = true;
                } else {
                    isRoundingUp = false;
                }
                updateTipAndTotal();
            }
        });
    }

    //calculates the tip and total from the bill amount and potential tip inputs
    //if splitting by person divides the total by the number of people
    //if rounding up and no splitting, rounds the total up to the nearest dollar
    //if rounding up and splitting the bill, rounds the per person up to the nearest dollar
    public void updateTipAndTotal() {
        if (isTipByPercent) tip = ((double)tipPercent/100)*billAmount;
        total = billAmount + tip;
        if (isRoundingUp && numPeople == 1) {
            total = Math.ceil(total);
        }
        perPersonTotal = total/numPeople;
        if (isRoundingUp && numPeople != 1) {
            perPersonTotal = Math.ceil(perPersonTotal);
        }
        textViewTipValue.setText(String.format(getResources().getString(R.string.string_money_value), tip));
        editTextTipValue.setText(String.format(getResources().getString(R.string.string_money_value), tip));
        textViewTotalValue.setText(String.format(getResources().getString(R.string.string_money_value), total));
        textViewPerPersonValue.setText(String.format(getResources().getString(R.string.string_money_value), perPersonTotal));
    }

}
