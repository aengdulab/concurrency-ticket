package com.aengdulab.ticket.service;

import com.aengdulab.ticket.domain.Member;
import com.aengdulab.ticket.domain.MemberTicket;
import com.aengdulab.ticket.domain.Ticket;
import com.aengdulab.ticket.repository.MemberRepository;
import com.aengdulab.ticket.repository.MemberTicketRepository;
import com.aengdulab.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberTicketService {

    private static final int MEMBER_TICKET_MAX_COUNT = 2;

    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final MemberTicketRepository memberTicketRepository;

    @Transactional
    public void issue(long memberId, long ticketId) {
        Member member = getMember(memberId);
        Ticket ticket = getTicket(ticketId);
        validateIssuable(member);
        memberTicketRepository.save(new MemberTicket(member, ticket));
        ticket.decrementQuantity();
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
    }

    private Ticket getTicket(long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("티켓이 존재하지 않습니다."));
    }

    private void validateIssuable(Member member) {
        int issuedMemberTicketCount = memberTicketRepository.countByMember(member);
        if (issuedMemberTicketCount >= MEMBER_TICKET_MAX_COUNT) {
            throw new IllegalArgumentException("계정당 구매할 수 있는 티켓 수량을 넘었습니다.");
        }
    }
}
