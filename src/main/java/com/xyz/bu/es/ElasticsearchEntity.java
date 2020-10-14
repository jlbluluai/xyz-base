package com.xyz.bu.es;

import lombok.Data;

/**
 * @author xyz
 * @date 2020/10/12
 */
@Data
public class ElasticsearchEntity {

    /**
     * id 不传用默认生成的（不建议）
     */
    private String id;

    /**
     * 数据体 建议传入实体类，不要用基本类型（保证key-value形式）
     */
    private Object data;

}
