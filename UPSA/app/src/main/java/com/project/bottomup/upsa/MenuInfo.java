package com.project.bottomup.upsa;

//입력된 메뉴를 관리하는 클래스
class MenuInfo {
    private String name;
    private int price;

    public MenuInfo(){
    }
    public MenuInfo(String name, int price){
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
