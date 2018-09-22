package com.yhd.arch.tuna.dao;

import com.ycache.redis.clients.jedis.BinaryClient;
import com.ycache.redis.clients.jedis.Jedis;
import com.ycache.redis.clients.jedis.JedisPubSub;
import com.ycache.redis.clients.jedis.ShardedJedisPipelineUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 9/9/16.
 */
public interface IRedisProxyDao {
	public ShardedJedisPipelineUtils buildPipelineUtils();
	/**
	 * list尾部添加元素，如果队列不存在，会创建新的队列
	 *
	 * @see link http://redis.cn/commands/rpush.html
	 * @return list长度
	 */
	public long rpush(String key, String string);
	public long rpush(byte[] key, byte[] string);

	/**
	 * list头部添加元素，如果队列不存在，会创建新的队列
	 *
	 * @see link http://redis.cn/commands/lpush.html
	 * @return list长度
	 */
	public long lpush(String key, String string);

	/**
	 * list尾部添加元素，如果队列不存在，返回0
	 *
	 * @see link http://redis.cn/commands/rpushx.html
	 * @return list长度
	 */
	public long rpushx(String key, String string);

	/**
	 * list头部添加元素，如果队列不存在，返回0
	 *
	 * @see link http://redis.cn/commands/lpushx.html
	 * @return list长度
	 */
	public long lpushx(String key, String string);

	/**
	 * 添加一个或者多个元素到集合(set)里
	 * @see link http://redis.cn/commands/sadd.html
	 * 返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
	 */
	public long sadd(String key, String... members);
	public long sadd(byte[] key, byte[]... members);
	/**
	 * 弹出头部元素
	 *
	 * @see link http://redis.cn/commands/lpop.html
	 * @return string 头部元素
	 */
	public String lpop(String key);

	/**
	 * 弹出尾部元素
	 *
	 * @see link http://redis.cn/commands/rpop.html
	 * @return string 尾部元素
	 */
	public String rpop(String key);

	/**
	 * 返回列表里的元素的索引 index 存储在 key 里面。 下标是从0开始索引的，所以 0 是表示第一个元素， 1 表示第二个元素，并以此类推。
	 * 负数索引用于指定从列表尾部开始索引的元素。在这种方法下，-1 表示最后一个元素，-2 表示倒数第二个元素，并以此往前推。
	 * 当 key 位置的值不是一个列表的时候，会返回一个error。
	 *
	 * @see link http://redis.cn/commands/lindex.html
	 * @return 请求的对应元素，或者当 index 超过范围的时候返回 nil。
	 */
	public String lindex(String key, long index);

	/**
	 * 把 value 插入存于 key 的列表中在基准值 pivot 的前面或后面。
	 * 当 key 不存在时，这个list会被看作是空list，任何操作都不会发生。
	 * 当 key 存在，但保存的不是一个list的时候，会返回error。
	 *
	 * @see link http://redis.cn/commands/linsert.html
	 * @return 经过插入操作后的list长度，或者当 pivot 值找不到的时候返回 -1。
	 */
	public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value);

	/**
	 * 设置队列里面一个元素的值
	 *
	 * @see link http://redis.cn/commands/lset.html
	 * @return 成功 OK 当index超出范围时会返回一个error。
	 */
	public String lset(String key, long index, String value);

	/**
	 * 获取LIST长度
	 *
	 * @see link http://redis.cn/commands/llen.html
	 * @return list长度
	 */
	public long llen(String key);

	/**
	 * 从存于 key 的列表里移除前 count 次出现的值为 value 的元素。 这个 count 参数通过下面几种方式影响这个操作：
	 * count > 0: 从头往尾移除值为 value 的元素。
	 * count < 0: 从尾往头移除值为 value 的元素。
	 * count = 0: 移除所有值为 value 的元素。
	 * 比如， LREM list -2 "hello" 会从存于 list 的列表里移除最后两个出现的 "hello"。
	 * 需要注意的是，如果list里没有存在key就会被当作空list处理，所以当 key 不存在的时候，这个命令会返回 0。
	 *
	 * @see link http://redis.cn/commands/lrem.html
	 * @return list长度
	 */
	public long lrem(String key, long count, String value);

	/**
	 * 修剪到指定范围内的清单
	 *
	 * @see link http://redis.cn/commands/ltrim.html
	 * @return OK
	 */
	public String ltrim(String key, long start, long end);

	/**
	 * 获取key这个List，从第几个元素到第几个元素 LRANGE key start
	 *
	 * @param key List别名
	 * @param start 开始下标
	 * @param end 结束下标
	 * @see link http://redis.cn/commands/lrange.html
	 * @return list
	 */
	public List<String> lrange(String key, long start, long end);

	/**
	 * 将哈希表key中的域field的值设为value。
	 *
	 * @param key 哈希表别名
	 * @param field 键
	 * @param value 值
	 * @see link http://redis.cn/commands/hset.html
	 * 1如果field是一个新的字段
	 * 0如果field原来在map里面已经存在
	 */
	public Long hset(String key, String field, String value);

	public Long hset(byte[] key, byte[] field, byte[] value);
	/**
	 * @param key
	 * @param value
	 * @see link http://redis.cn/commands/set.html
	 * @return 总是OK，因为SET不会失败。
	 */
	public String set(String key, String value);

	/**
	 * 获取key的值
	 *
	 * @param key
	 * @see link http://redis.cn/commands/get.html
	 * @return value
	 */
	public String get(String key);

	/**
	 * 存入序列化的数据
	 *
	 * @param key 序列化后的KEY
	 * @param value  序列化后的对象
	 * @return 总是OK，因为SET不会失败。
	 */
	public String set(byte[] key, byte[] value);

	/**
	 * 取得序列化的数据
	 *
	 * @param key 序列化后的KEY
	 * @return byte[] value
	 */
	public byte[] get(byte[] key);

	/**
	 * 获取key的有效时间（单位：秒）
	 *
	 * @param key
	 * @see link http://redis.cn/commands/ttl.html
	 * @return 获取key的有效时间（单位：秒）
	 */
	public long ttl(String key);

	/**
	 * 删除key的值
	 *
	 * @param key
	 * @see link http://redis.cn/commands/del.html
	 * @return 被删除的key的数量
	 */
	public long del(String key);

	/**
	 * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。
	 * 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联
	 * @param key
	 * @param map
	 * @see link http://redis.cn/commands/hmset.html
	 * @return OK
	 */
	public String hmset(String key, Map<String, String> map);
	public String hmset(byte[] key, Map<byte[], byte[]> map);

	/**
	 * 设置key对应字符串value，并且设置key在给定的seconds时间之后超时过期。这个命令等效于执行下面的命令：
	 * SET mykey value
	 * EXPIRE mykey seconds
	 * @param key
	 * @param seconds
	 *            生命周期 秒为单位
	 * @param value
	 * @see link http://redis.cn/commands/setex.html
	 * @return OK
	 */
	public String setex(String key, int seconds, String value);

	/**
	 * 为给定key设置生命周期
	 *
	 * @param key
	 * @param seconds 生命周期 秒为单位
	 * @see link http://redis.cn/commands/expire.html
	 * @return 1 如果设置了过期时间, 0 如果没有设置过期时间，或者不能设置过期时间
	 */
	public Long expire(String key, int seconds);

	/**
	 * 为给定key设置过期时间戳
	 *
	 * @param key
	 * @param unixTime UNIX 时间戳 Unix timestamp
	 * @see link http://redis.cn/commands/expireAt.html
	 * @return 1 如果设置了过期时间,0 如果没有设置过期时间，或者不能设置过期时间
	 */
	public Long expireAt(String key, long unixTime);

	/**
	 * 检查key是否存在
	 *
	 * @param key
	 * @see link http://redis.cn/commands/exists.html
	 * @return true/false
	 */
	public boolean exists(String key);

	/**
	 * 返回key值的类型 none(key不存在),string(字符串),list(列表),set(集合),zset(有序集),hash(哈希表)
	 *
	 * @param key
	 * @see link http://redis.cn/commands/type.html
	 * @return string, list, set, zset and hash, or none
	 */
	public String type(String key);

	/**
	 * 从哈希表key中获取field的value
	 *
	 * @param key
	 * @param field
	 * @see link http://redis.cn/commands/hget.html
	 * @return 该字段所关联的值。当字段不存在或者 key 不存在时返回nil。
	 */
	public String hget(String key, String field);

	/**
	 * 返回哈希表key中，所有的域和值 默认无序
	 *
	 * @param key
	 * @see link http://redis.cn/commands/hgetall.html
	 * @return 哈希集中字段和值的列表。当 key 指定的哈希集不存在时返回空列表。
	 */
	public Map<String, String> hgetAll(String key);

	/**
	 * 返回哈希表key中，所有的域和值
	 *
	 * @param key
	 * @param order 是否保持原始顺序
	 * @see link http://redis.cn/commands/hgetall.html
	 * @return 哈希集中字段和值的列表。当 key 指定的哈希集不存在时返回空列表。
	 */
	public Map<String, String> hgetAll(String key, boolean order);

	/**
	 * 返回哈希表key中，所有的域和值 默认有序
	 *
	 * @param key
	 * @see link http://redis.cn/commands/hgetall.html
	 * @return 哈希集中字段和值的列表。当 key 指定的哈希集不存在时返回空列表。
	 */
	public Map<String, String> hgetAllToLinkedHashMap(String key);

	/**
	 * 返回key集合所有的元素
	 *
	 * @param key
	 * @see link http://redis.cn/commands/smembers.html
	 * @return 集合中的所有元素
	 */
	public Set<?> smembers(String key);

	/**
	 * 移除集合中的member元素
	 *
	 * @param key  List别名
	 * @param field 键
	 * @see link http://redis.cn/commands/srem.html
	 * @return 从集合中移除元素的个数，不包括不存在的成员
	 */
	public Long srem(String key, String... field);

	/**
	 * 返回集合存储的key的基数 (集合元素的数量).
	 *
	 * @param key  List别名
	 * @see link http://redis.cn/commands/scard.html
	 * return 集合的基数(元素的数量),如果key不存在,则返回 0.
	 */
	public Long scard(String key);

	/**
	 * 移除并且返回集合中的随机元素
	 *
	 * @param key  List别名
	 * @see link http://redis.cn/commands/spop.html
	 * return 被移除的元素, 当key不存在的时候返回 nil
	 */
	public String spop(String key);

	/**
	 * 返回集合中的随机元素
	 *
	 * @param key  List别名
	 * @see link http://redis.cn/commands/srandmember.html
	 * return 集合中的随机元素,当key不存在的时候返回 nil
	 */
	public String srandmember(String key);

	/**
	 * 判断member元素是否是集合key的成员。是（true），否则（false）
	 *
	 * @param key
	 * @param field
	 * @see link http://redis.cn/commands/sismember.html
	 * @return true/false
	 */
	public boolean sismember(String key, String field);

	/**
	 * 如果 key 已经存在，并且值为字符串，那么这个命令会把 value 追加到原来值（value）的结尾。
	 * 如果 key 不存在，那么它将首先创建一个空字符串的key，再执行追加操作，这种情况 APPEND 将类似于 SET 操作。
	 *
	 * @param key
	 * @param value
	 * @see link http://redis.cn/commands/append.html
	 * @return 返回append后字符串值（value）的长度。
	 */
	public Long append(String key, String value);

	/**
	 * -- key
	 *
	 * @param key
	 * @see link http://redis.cn/commands/decr.html
	 * @return 减小之后的value
	 */
	public Long decr(String key);

	/**
	 * key 减指定数值
	 *
	 * @param key
	 * @see link http://redis.cn/commands/decrBy.html
	 * @return 减小之后的value
	 */
	public Long decrBy(String key, Integer integer);

	/**
	 * 这里的N是返回的string的长度。复杂度是由返回的字符串长度决定的，但是因为从一个已经存在的字符串创建一个子串是很容易的，所以对于较小的字符串，
	 * 可以认为是O(1)的复杂度。
	 *
	 * @param key
	 * @see link http://redis.cn/commands/getrange.html
	 * @return 返回的string
	 */
	public String getrange(String key, int startOffset, int endOffset);

	/**
	 * 自动将key对应到value并且返回原来key对应的value。如果key存在但是对应的value不是字符串，就返回错误。
	 *
	 * @param key
	 * @param value
	 * @see link http://redis.cn/commands/getSet.html
	 * @return 原来key对应的value。如果key存在但是对应的value不是字符串，就返回错误
	 */
	public String getSet(String key, String value);

	/**
	 * 从 key 指定的哈希集中移除指定的域。在哈希集中不存在的域将被忽略。如果 key
	 * 指定的哈希集不存在，它将被认为是一个空的哈希集，该命令将返回0。
	 *
	 * 返回值 整数：返回从哈希集中成功移除的域的数量，不包括指出但不存在的那些域
	 *
	 *
	 *
	 * @param key
	 * @param fields
	 * @see link http://redis.cn/commands/hdel.html
	 * @return 返回从哈希集中成功移除的域的数量，不包括指出但不存在的那些域
	 */
	public Long hdel(String key, String... fields);

	/**
	 * 返回字段是否是 key 指定的哈希集中存在的字段。
	 *
	 * 返回值 整数, 含义如下：
	 *
	 * 1 哈希集中含有该字段。 0 哈希集中不含有该存在字段，或者key不存在。
	 *
	 * @param key
	 * @param fields
	 * @see link http://redis.cn/commands/hexists.html
	 * @return 1 哈希集中含有该字段。 0 哈希集中不含有该存在字段，或者key不存在
	 */
	public Boolean hexists(String key, String fields);

	/**
	 * 增加 key 指定的哈希集中指定字段的数值。如果 key 不存在，会创建一个新的哈希集并与 key
	 * 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0
	 *
	 * HINCRBY 支持的值的范围限定在 64位 有符号整数
	 *
	 * 返回值 整数：增值操作执行后的该字段的值。
	 *
	 * @param key
	 * @param fields
	 * @param value
	 * @see link http://redis.cn/commands/hincrBy.html
	 * @return 增值操作执行后的该字段的值
	 */
	public Long hincrBy(String key, String field, int value);

	/**
	 * 返回 key 指定的哈希集中所有字段的名字。
	 *
	 * 返回值 多个返回值：哈希集中的字段列表，当 key 指定的哈希集不存在时返回空列表。
	 * @param key
	 *
	 * @see link http://redis.cn/commands/hkeys.html
	 * @return 哈希集中的字段列表
	 */
	public Set<String> hkeys(String key);

	/**
	 * 返回 key 指定的哈希集包含的字段的数量。
	 *
	 * 返回值 整数：哈希集中字段的数量，当 key 指定的哈希集不存在时返回 0
	 * @param key
	 * @see link http://redis.cn/commands/hlen.html
	 * @return 哈希集中字段的数量
	 */
	public Long hlen(String key);

	/**
	 * 返回 key 指定的哈希集中指定字段的值。
	 *
	 * 对于哈希集中不存在的每个字段，返回 nil 值。因为不存在的keys被认为是一个空的哈希集，对一个不存在的 key 执行 HMGET
	 * 将返回一个只含有 nil 值的列表
	 *
	 * 返回值 多个返回值：含有给定字段及其值的列表，并保持与请求相同的顺序。
	 * @param key
	 * @param fields
	 * @see link http://redis.cn/commands/hmget.html
	 * @return 含有给定字段及其值的列表，并保持与请求相同的顺序。
	 */
	public List<String> hmget(String key, String... fields);

	/**
	 * 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key
	 * 关联。如果字段已存在，该操作无效果。
	 *
	 * 返回值 整数：含义如下
	 *
	 * 1：如果字段是个新的字段，并成功赋值 0：如果哈希集中已存在该字段，没有操作被执行
	 * @param key
	 *
	 * @see link http://redis.cn/commands/hsetnx.html
	 * @return 1：如果字段是个新的字段，并成功赋值 0：如果哈希集中已存在该字段，没有操作被执行
	 */
	public Long hsetnx(String key, String field, String value);

	/**
	 * 返回 key 指定的哈希集中所有字段的名称。
	 *
	 * 返回值 多个返回值：哈希集中的值的列表，当 key 指定的哈希集不存在时返回空列表。
	 *
	 *
	 *
	 * @param key
	 *
	 * @see link http://redis.cn/commands/hvals.html
	 * @return 返回 key 指定的哈希集中所有字段的名称。
	 */
	public List<String> hvals(String key);

	/**
	 * ++key
	 *
	 * @param key
	 *
	 * @see link http://redis.cn/commands/incr.html
	 * @return 增加之后的value
	 */
	public Long incr(String key);


	/**
	 * 参选，如果参选成功将为key续期,如果已有被选举人则判断是否为自己 为自己则续期,setnx可以用这个方法
	 *
	 * @param key
	 *            参选项目
	 * @param candidates
	 *            候选人
	 * @param timeOut
	 *            参选成功后的过期时间
	 * @return true 参选成功并续期成功 或 被选举人为自己并续期成功， false 参选失败或被选举人不为自己
	 */
	public boolean electioneer(String key, String candidates, int timeOut);

	/**
	 *
	 * 将key对应的数字加decrement。如果key不存在，操作之前，key就会被置为0。
	 * 如果key的value类型错误或者是个不能表示成数字的字符串，就返回错误。这个操作最多支持64位有符号的正型数字。
	 *
	 * 查看命令INCR了解关于增减操作的额外信息。
	 *
	 * 返回值 数字：增加之后的value值。
	 *
	 *
	 * @param key
	 * @param integer
	 * @see link http://redis.cn/commands/incrBy.html
	 * @return 增加之后的value
	 */
	public Long incrBy(String key, int integer);
	/**
	 * 订阅指定shardkey下的某些频道
	 *
	 * @param shardkey
	 *            用于shard定向
	 * @param jedisPubSub
	 *            用于回调
	 * @param channels
	 *            频道名称
	 * @see link http://redis.cn/commands/subscribe.html
	 */
	public void subscribe(String shardkey, JedisPubSub jedisPubSub,
			String... channels);
	/**
	 * 向指定shardkey下的某频道发送消息
	 *
	 * @param shardkey
	 *            用于shard定向
	 * @param channel
	 *            频道名称
	 * @param message
	 *            消息内容
	 * @see link http://redis.cn/commands/publish.html
	 */
	public void publish(String shardkey, String channel, String message);
	/**
	 * 根据shardKey获取jedis
	 *
	 * @param shardKey
	 */
	public Jedis getJedisByShardKey(String shardKey);

	/**
	 * 获取key在 shard分片中的index
	 *
	 * @param uri
	 * @param key
	 * @return
	 */
	public int getShardIndex(String uri, String key);
}
