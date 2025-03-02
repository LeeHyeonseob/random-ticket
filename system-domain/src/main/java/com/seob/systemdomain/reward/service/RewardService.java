package com.seob.systemdomain.reward.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.reward.domain.RewardDomain;

public interface RewardService {
    RewardDomain assignReward(EntryDomain entryDomain);
}
