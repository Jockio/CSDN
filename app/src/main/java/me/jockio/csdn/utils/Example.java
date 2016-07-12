//public class MainActivity extends AppCompatActivity {
//    //当前fragment的tag
//    private String mCurrentFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (savedInstanceState == null) {
//            mCurrentFragment = HomeFragment.class.getName();
//            getFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, new HomeFragment(), mCurrentFragment)
//                    .commit();
//        }
//
//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        assert radioGroup != null;
//        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
//    }
//
//    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
//
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            Class<?> fClass = null;
//
//            switch (checkedId) {
//                case R.id.home_button:
//                    fClass = HomeFragment.class;
//                    break;
//                case R.id.settings_button:
//                    fClass = SettingsFragment.class;
//                    break;
//                default:
//                    break;
//            }
//
//            if (fClass != null) {
//                String fTag = fClass.getName();
//                FragmentManager fManager = getFragmentManager();
//                FragmentTransaction fTransaction = fManager.beginTransaction();
//                Fragment oldFragment = fManager.findFragmentByTag(mCurrentFragment);
//                Fragment newFragment = fManager.findFragmentByTag(fTag);
//                if (newFragment == null || !newFragment.isAdded()) {
//                    try {
//                        newFragment = (Fragment) fClass.newInstance();
//                        fTransaction.add(R.id.fragment_container, newFragment, fClass.getName());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                mCurrentFragment = fClass.getName();
//                fTransaction.show(newFragment).hide(oldFragment).commit();
//            }
//        }
//    }
//}
