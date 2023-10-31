package com.example.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface  DocMapperCust {
    public void IncreaseViewCount(@Param("id") Long id);

    void IncreaseVoteCount(@Param("id") Long id);


}
