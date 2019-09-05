package disorderly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DisorderlyEscape {
    
    public static String solution(int w, int h, int s) {
        List<List<List<Integer>>> partitions = generatePartitions(w,h);
        
        List<List<PartitionPerm>> partsNPerms = partitions.stream().map(lists -> //not a bad name for a barber shop
            lists.stream().map(list -> 
                new PartitionPerm(list, permsOfPartition(list)))
                .collect(Collectors.toCollection(ArrayList::new)))
            .collect(Collectors.toCollection(ArrayList::new));
        
        BigInteger accum = BigInteger.ZERO;
        BigInteger factProd = fact(w).multiply(fact(h));
        for (PartitionPerm partPerm1 : partsNPerms.get(0)) {
            for (PartitionPerm partPerm2 : partsNPerms.get(1)) {
                BigInteger multAccum = BigInteger.ONE;
                
                for (int pSize1=0; pSize1<partPerm1.part.size(); pSize1++) {
                    Integer pMult1 = partPerm1.part.get(pSize1);
                    if (pMult1 != 0) {
                        for (int pSize2=0; pSize2<partPerm2.part.size(); pSize2++) {
                            Integer pMult2 = partPerm2.part.get(pSize2);
                            if (pMult2 != 0) {
                                BigInteger toAdd = BigInteger.valueOf(s).pow(GCD(pSize1+1, pSize2+1)).pow(pMult1*pMult2);
                                multAccum = multAccum.multiply(toAdd);
                            }
                        }
                    }
                }
                
                multAccum = multAccum.multiply(factProd.divide(partPerm1.perms.multiply(partPerm2.perms)));
                accum = accum.add(multAccum);
            }
        }
        
        return accum.divide(factProd).toString();
    }
    
    private static List<List<List<Integer>>> generatePartitions(int w, int h){
        List<List<List<Integer>>> partitions = new ArrayList<>();
        
        List<List<Integer>> initialList = new ArrayList<>();
        initialList.add(new ArrayList<>(Arrays.asList(1)));
        partitions.add(initialList);
        
        //partitions of partNum with largest partition n stored @ index: (partNum)*(partNum-1)/2-1+n
        
        int lim = Math.max(w, h);
        for (int partNum = 2; partNum <= lim; partNum++) {
            for (int maxLargestPart = 1; maxLargestPart < partNum; maxLargestPart++) {
                List<List<Integer>> curParts = new ArrayList<>();
                for (int largestPart = Math.min(maxLargestPart, partNum-maxLargestPart); largestPart >= 1; largestPart--) {
                    int base = (partNum-maxLargestPart)*(partNum-maxLargestPart-1)/2-1;
                    List<List<Integer>> parts = partitions.get(base + largestPart);
                    for (List<Integer> part : parts) {
                        List<Integer> newPart = new ArrayList<>(part);
                        while (newPart.size() < maxLargestPart) {
                            newPart.add(0);
                        }
                        newPart.set(maxLargestPart-1, newPart.get(maxLargestPart-1)+1);
                        curParts.add(newPart);
                    }
                }
                partitions.add(curParts);
            }
            List<List<Integer>> parts = new ArrayList<>();
            List<Integer> part = new ArrayList<>();
            while (part.size() < partNum-1) {
                part.add(0);
            }
            part.add(1);
            parts.add(part);
            partitions.add(parts);
        }
        
        List<List<Integer>> wParts = new ArrayList<>();
        List<List<Integer>> hParts;
        
        int base = w*(w-1)/2;
        for (int i=0; i<w; i++) {
            wParts.addAll(partitions.get(base+i));
        }
        
        if (h == w) {
            hParts = wParts;
        } else {
            hParts = new ArrayList<>();
            base = h*(h-1)/2;
            for (int i=0; i<h; i++) {
                hParts.addAll(partitions.get(base+i));
            }
        }
        
        return new ArrayList<>(Arrays.asList(wParts, hParts));
    }
    
    private static BigInteger permsOfPartition(List<Integer> part) {
        BigInteger count = BigInteger.ONE;
        
        for (int partSize=1; partSize <= part.size(); partSize++) {
            int numParts = part.get(partSize-1);
            count = count.multiply(fact(numParts));
            count = count.multiply(BigInteger.valueOf(partSize).pow(numParts));
        }
        
        return count;
    }
    
    private static int GCD(int a, int b) {
        int[] vals = {a, b};
        int ind = (a < b) ? 0 : 1;
        
        while (vals[ind] != 0) {
            vals[1-ind] %= vals[ind];
            ind = 1-ind;
        }
        
        return vals[1-ind];
    }
    
    private static BigInteger fact(int a) {
        BigInteger res = BigInteger.ONE;
        
        while (a > 1) {
            res = res.multiply(BigInteger.valueOf(a--));
        }
        
        return res;
    }
    
    private static class PartitionPerm{
        List<Integer> part;
        BigInteger perms;
        
        public PartitionPerm(List<Integer> part, BigInteger perms) {
            this.part = part;
            this.perms = perms;
        }
    }
    
    public static void main(String[] args) {
        Long time = System.currentTimeMillis();
        String res = solution(12,12,5);
        System.out.println(res);
        System.out.println((System.currentTimeMillis() - time) + " ms");
    }
}
