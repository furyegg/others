package com.lombardrisk.xbrl.checker.ejb.email;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.lombardrisk.xbrl.checker.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cesar on 21/05/2014.
 */

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EmailIpQuotaManager implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(EmailIpQuotaManager.class);

    private HouseKeptSortedSetMultiMap<String, Calendar> ipMap;

    @PostConstruct
    public void init() {
        final TreeMultimap<String, Calendar> sortedSetMultiMap = TreeMultimap.create(Ordering.natural(), Ordering.natural().reverse());
        ipMap = new HouseKeptSortedSetMultiMap<>(sortedSetMultiMap);
    }

    public synchronized boolean checkForIpQuotas(String ip) {
        final int maxAmount = Config.INSTANCE.getInt("email.max.per.ip.amount");
        final long maxIntervalAmount = Config.INSTANCE.getLong("email.max.per.ip.interval.amount");
        final TimeUnit maxIntervalUnit = TimeUnit.valueOf(Config.INSTANCE.getString("email.max.per.ip.interval.unit"));
        final Calendar now = Calendar.getInstance();
        now.setTimeInMillis(now.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(maxIntervalAmount, maxIntervalUnit));

        final SortedSet<Calendar> requests = ipMap.houseKeepTail(ip, now);
        if (requests.size() >= maxAmount) {
            log.info(MessageFormat.format("Maximum number of request reached for ip {0}. Max allowed is {1} per {2} {3}"
                    , ip, maxAmount, maxIntervalAmount, maxIntervalUnit));
            return false;
        } else {
            ipMap.put(ip, Calendar.getInstance());
            return true;
        }
    }
}
