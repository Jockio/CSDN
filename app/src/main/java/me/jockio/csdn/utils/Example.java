public MyRecyclerViewAdapter(){
    mImageLoader = ImageLoader.getInstance(MyApplication.getContext());
    itemList = new ArrayList<>();
}

//开启线程下载图片
mImageLoader.addTask(item.getImageUrl(), ((MyViewHolder)holder).imageView);
        
List<PictureItem> list = new ArrayList<>();
if((list = readFromFile(list)) != null){
    Toast.makeText(MyApplication.getContext(), "size = " + list.size(), Toast.LENGTH_SHORT).show();
    adapter.addPictureList(list);
}else{
    swipeRefreshLayout.setRefreshing(true);
    new Thread(new MyRunnable()).start();
}

/**
 * 刚打开APP时，从文件中读取数据显示在屏幕上
 *
 * @return
 */
private List<PictureItem> readFromFile(List<PictureItem> list) {
    File file = new File(getDirectory(), "list.cache");
    ObjectInputStream ois = null;
    try {
        if (!file.exists()) return null;
        ois = new ObjectInputStream(new FileInputStream(file));
        list = (List<PictureItem>)ois.readObject();
        return list;
    } catch (IOException e) {
        e.printStackTrace();
    }catch (ClassNotFoundException e) {
        e.printStackTrace();
    }finally {
        if(ois != null){
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return null;
}

/**
 * 将数据写入文件
 *
 * @return
 */
private static boolean writeToFile(List<PictureItem> list) {
    File file = new File(getDirectory(), "list.cache");
    ObjectOutputStream oos = null;
    try {
        if (!file.exists()) {
            file.createNewFile();
        }
        oos = new ObjectOutputStream(new FileOutputStream(file));
        if(list != null){
            oos.writeObject(list);
        }
        return true;
    } catch (IOException e) {
        e.printStackTrace();
    }finally{
        if(oos != null){
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return false;
}

/**
 * 获得缓存目录
 **/
private static String getDirectory() {
    String dir = getSDPath() + "/" + CACHDIR;
    return dir;
}

/**
 * 取SD卡路径
 **/
private static String getSDPath() {
    File sdDir = null;
    boolean sdCardExist = Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
    if (sdCardExist) {
        sdDir = Environment.getExternalStorageDirectory();  //获取根目录
    }
    if (sdDir != null) {
        return sdDir.toString();
    } else {
        return "";
    }
}

public class MainActivity extends AppCompatActivity {
    //当前fragment的tag
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mCurrentFragment = HomeFragment.class.getName();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), mCurrentFragment)
                    .commit();
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        assert radioGroup != null;
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Class<?> fClass = null;

            switch (checkedId) {
                case R.id.home_button:
                    fClass = HomeFragment.class;
                    break;
                case R.id.settings_button:
                    fClass = SettingsFragment.class;
                    break;
                default:
                    break;
            }

            if (fClass != null) {
                String fTag = fClass.getName();
                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransaction = fManager.beginTransaction();
                Fragment oldFragment = fManager.findFragmentByTag(mCurrentFragment);
                Fragment newFragment = fManager.findFragmentByTag(fTag);
                if (newFragment == null || !newFragment.isAdded()) {
                    try {
                        newFragment = (Fragment) fClass.newInstance();
                        fTransaction.add(R.id.fragment_container, newFragment, fClass.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mCurrentFragment = fClass.getName();
                fTransaction.show(newFragment).hide(oldFragment).commit();
            }
        }
    }
}
