package com.dimje.domain.usecase

class AlreadySubmittedTodayException : IllegalStateException("오늘의 고민은 이미 기록했습니다.")
