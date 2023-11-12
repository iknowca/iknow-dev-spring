package xyz.iknowca.iknowdevspring.domain.notice.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.iknowca.iknowdevspring.domain.notice.entity.Notice

interface NoticeRepository:JpaRepository<Notice, Long> {
}