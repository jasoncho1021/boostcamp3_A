package com.aone.menurandomchoice.repository.local.db;

public enum StoreTable {

    TABLE_NAME("STORES"),
    STORES_IDX("store_idx"),
    STORES_NAME("name"),
    STORES_OPENTIME("opentime"),
    STORES_CLOSETIME("closetime"),
    STORES_ADDRESS("address"),
    STORES_DESCRIPTION("description"),
    STORES_LATITUDE("latitude"),
    STORES_LONGITUDE("longitude"),
    STORES_UPDATETIME("update_time");

    private String columnName;

    StoreTable(String columnName){
        this.columnName = columnName;
    }

    String getColumnName(){
        return this.columnName;
    }

}
