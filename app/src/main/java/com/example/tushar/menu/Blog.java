package com.example.tushar.menu;

/**
 * Created by TUSHAR on 18-03-2017.
 */

 class Blog {
    private String Title;
    private String Description;
    private String Image;
    private String Profile;
    private String name;
  public Blog(){

  }
    public Blog(String Title, String Description, String Image,String Profile,String name) {
       this.Title = Title;
       this.Description = Description;
        this.Image = Image;
        this.Profile=Profile;
        this.name=name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDesc(String desc) {
        this.Description = desc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }
   public void setProfile(String Profile){
       this.Profile=Profile;
   }
    public String getProfile(){
        return Profile;
    }


    public String getName() {
        return "Posted by :" + name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
